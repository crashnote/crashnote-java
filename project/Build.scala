import sbt._
import sbt.Keys._

object Build
    extends sbt.Build with Projects {

    import Dependency._
    import Dependencies._

    lazy val crashnote =
        Project(id = "crashnote", base = file("."))
            .configs(UnitTest, FuncTest)
            .settings(moduleSettings: _*)
            .aggregate(servletNotifier, appengineNotifier, coreModule, loggerModule, servletModule, testModule)


    // ### Notifiers ------------------------------------------------------------------------------

    lazy val servletNotifier =
        NotifierProject("servlet", "Crashnote Servlet Notifier",
            withModules = Seq(servletModule),
            withLibs = loggerKit ++ List(Provided.servlet),
            withSources = servletSrc)
            .settings(description := "Reports exceptions from Java servlet apps to crashnote.com")

    lazy val appengineNotifier =
        NotifierProject("appengine","Crashnote Appengine Notifier",
            withModules = Seq(servletModule),
            withLibs = loggerKit ++ List(Provided.servlet, Provided.appengine),
            withSources = servletSrc)
            .settings(normalizedName := "crashnote-appengine")
            .settings(description := "Reports exceptions from Java apps on Appengine to crashnote.com")


    // ### Modules --------------------------------------------------------------------------------

    // Internal

    lazy val coreModule =
        ModuleProject("core", withModules = Seq(jsonModule, configModule))

    lazy val loggerModule =
        ModuleProject("logger",
            withModules = Seq(coreModule), withLibs = loggerKit)

    lazy val servletModule =
        ModuleProject("servlet",
            withModules = Seq(loggerModule), withLibs = Seq(Provided.servlet))

    // External

    lazy val jsonModule =
        ModuleProject("ext-json")

    lazy val configModule =
        ModuleProject("ext-config")
}


// ### Project ------------------------------------------------------------------------------------

trait Projects
    extends Settings {

    self: Build =>

    import Dependencies._

    type ModRef = ClasspathDep[ProjectReference]

    object ModuleProject {
        def apply(name: String,
                  withModules: Seq[ModRef] = Seq(), withLibs: Seq[ModuleID] = Seq()) =
            Project("module-" + name, file("modules/" + name))
                .configs(UnitTest, FuncTest)
                .settings(moduleSettings: _*)
                .settings(libraryDependencies := withLibs)
                .dependsOn((Seq(testModule % "test->test") ++ withModules): _*)
    }

    object NotifierProject {
        def apply(name: String, displayName: String,
                  withModules: Seq[ModRef], withLibs: Seq[ModuleID] = Seq(), withSources: Seq[String] = Seq()) =
            Project("crashnote-" + name, file(name))
                .configs(UnitTest, FuncTest)
                .settings(notifierSettings: _*)
                .settings(libraryDependencies := withLibs)
                .settings(normalizedName := displayName)
                .settings(unmanagedSourceDirectories in Compile <++= modulesSources(withSources: _*))
                .settings(unmanagedResourceDirectories in Compile <++= modulesResources(withSources: _*))
                .dependsOn((Seq(testModule % "test->test") ++ withModules): _*)
    }

    lazy val testModule =
        Project("module-test", file("modules/test"))
            .settings(libraryDependencies ++= testKit)
            .settings(moduleSettings: _*)
}


// ### Settings -----------------------------------------------------------------------------------

trait Settings {

    self: Build =>

    lazy val buildSettings = Seq(
        organization := "com.crashnote",
        version := "0.2.2",

        startYear := Some(2011),
        licenses +=("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

        organizationName := "101 Loops",
        organizationHomepage := Some(url("http://www.101loops.com"))
    )

    lazy val testSettings = Seq(
        testOptions in Test := Seq(Tests.Filter((n:String) => unitFilter(n) || funcFilter(n))),
        testOptions in FuncTest := Seq(Tests.Filter(funcFilter)),
        testOptions in UnitTest := Seq(Tests.Filter(unitFilter))
    )

    lazy val baseSettings =
        Defaults.defaultSettings ++ buildSettings ++ testSettings ++ Licenses.licenseSettings ++ Seq(
            crossPaths := false,
            scalaVersion := "2.9.1",

            resolvers += "spray repo" at "http://repo.spray.cc/",
            resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",

            javacOptions += "-g:none",
            javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
            //javacOptions ++= Seq("-bootclasspath", (javaDir / "jre" / "lib" / "rt.jar").getAbsolutePath)

            javaHome := Some(javaDir)
        )

    lazy val moduleSettings =
        baseSettings ++ Seq(publish := false, publishLocal := false)

    lazy val notifierSettings =
        baseSettings ++ About.aboutSettings ++ Publish.settings

    // Test-related

    lazy val FuncTest = config("func") extend (Test)
    lazy val UnitTest = config("unit") extend (Test)

    private def unitFilter(name: String): Boolean =
        name contains ".unit."

    private def funcFilter(name: String): Boolean =
        name contains ".func."

    // Source-related

    lazy val servletSrc = Seq("ext-json", "ext-config", "core", "logger", "servlet")

    def modulesSources(mods: String*) =
        modulesSrcDir("java", mods)

    def modulesResources(mods: String*) =
        modulesSrcDir("resources", mods)

    private def modulesSrcDir(typeOf: String, mods: Seq[String]) =
        baseDirectory(d => mods.map(d / ".." / "modules" / _ / "src" / "main" / typeOf))

    // Path-related

    lazy val javaDir =
        file(Option(System.getenv("JAVA6_HOME")).getOrElse(System.getenv("JAVA_HOME")))
}


// ### Dependencies -------------------------------------------------------------------------------

object Dependencies {

    import Dependency._

    lazy val loggerKit =
        Seq(Provided.slf4j, Provided.log4j, Provided.logback)

    lazy val testKit =
        Seq(Test.junit, Test.specs2, Test.mockito, Test.commonsIO,
            Test.jetty, Test.akka, Test.sprayClient, Test.sprayServer)
}

object Dependency {

    object Provided {
        val slf4j = "org.slf4j" % "slf4j-api" % "1.6.0" % "provided"

        val log4j = "log4j" % "log4j" % "1.2.16" % "provided"
        val logback = "ch.qos.logback" % "logback-classic" % "1.0.0" % "provided"

        val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
        val appengine = "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.0" % "provided"
    }

    object Test {
        val junit = "junit" % "junit" % "4.10" % "test"
        val specs2 = "org.specs2" % "specs2_2.9.1" % "1.9" % "test"
        val mockito = "org.mockito" % "mockito-all" % "1.9.0" % "test"
        val commonsIO = "commons-io" % "commons-io" % "2.3" % "test"

        val jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.5.1.v20110908" % "test"

        val akka = "se.scalablesolutions.akka" % "akka-actor" % "1.3.1" % "test"
        val sprayServer = "cc.spray" % "spray-server" % "0.9.0" % "test"
        val sprayClient = "cc.spray" % "spray-client" % "0.9.0" % "test"
    }

}


// ### DEPLOYING ----------------------------------------------------------------------------------

object Publish {

    final val Snapshot = "-SNAPSHOT"

    lazy val settings =
        Seq(
            publishMavenStyle := true,
            publishArtifact in Test := false,

            otherResolvers ++= Seq(Resolver.file("m2", file(Path.userHome + "/.m2/repository"))),
            publishLocalConfiguration <<= (packagedArtifacts, deliverLocal, checksums, ivyLoggingLevel) map {
                (arts, _, chks, level) => new PublishConfiguration(None, "m2", arts, chks, level)
            },

            publishTo <<= version {
                (v: String) =>
                    val nexus = "https://oss.sonatype.org/"
                    if (v.trim.endsWith(Snapshot))
                        Some("snapshot" at nexus + "content/repositories/snapshots")
                    else
                        Some("release" at nexus + "service/local/staging/deploy/maven2")
            },

            credentials += Credentials(Path.userHome / ".sbt" / "sonatype"),

            pomIncludeRepository := {
                x => false
            },

            makePomConfiguration ~= {
                (mpc: MakePomConfiguration) =>
                    mpc.copy(configurations = Some(Seq(Provided)))
            },

            pomExtra :=
                <url>http://www.crashnote.com</url>

                    <developers>
                        <developer>
                            <email>dev@101loops.com</email>
                            <name>101 Loops Developers</name>
                            <organization>101 Loops</organization>
                            <organizationUrl>http://www.101loops.com</organizationUrl>
                        </developer>
                    </developers>

                    <scm>
                        <url>http://github.com/crashnote/crashnote-java</url>
                        <connection>scm:git:https://github.com/crashnote/crashnote-java.git</connection>
                        <developerConnection>
                            scm:git:git@github.com:crashnote/crashnote-java.git
                        </developerConnection>
                    </scm>

                    <issueManagement>
                        <system>github</system>
                        <url>https://github.com/crashnote/crashnote-java/issues</url>
                    </issueManagement>
        )
}