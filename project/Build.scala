import sbt._
import sbt.Keys._

object Build extends sbt.Build with Settings with Commands {

    import Dependency._
    import Dependencies._

    lazy val crashnote =
        Project(id = "crashnote", base = file("."))
            .settings(commands += intellij)
            .aggregate(servletNotifier, appengineNotifier, coreModule, loggerModule, servletModule, testModule)

    // ### Notifiers ------------------------------------------------------------------------------

    lazy val servletNotifier =
        Project("Crashnote Servlet Notifier", file("servlet"))
            .settings(notifierSettings: _*)
            .settings(normalizedName := "notifier-servlet")
            .settings(description := "Reports exceptions from Java servlet apps to crashnote.com")
            .settings(libraryDependencies := loggerKit ++ List(Provided.servlet))
            .settings(unmanagedSourceDirectories in Compile <<= moduleSources("core", "logger", "servlet"))
            .dependsOn(servletModule, testModule % "test->test") // servletModule

    lazy val appengineNotifier =
        Project("Crashnote Appengine Notifier", file("appengine"))
            .settings(notifierSettings: _*)
            .settings(normalizedName := "notifier-appengine")
            .settings(description := "Reports exceptions from Java apps on Appengine to crashnote.com")
            .settings(libraryDependencies := loggerKit ++ List(Provided.servlet, Provided.appengine))
            .settings(unmanagedSourceDirectories in Compile <<= moduleSources("core", "logger", "servlet"))
            .dependsOn(servletModule, testModule % "test->test")


    // ### Modules --------------------------------------------------------------------------------

    lazy val coreModule =
        Project("module-core", file("modules/core"))
            .settings(moduleSettings: _*)
            .settings(libraryDependencies := List())
            .dependsOn(testModule % "test->test")

    lazy val loggerModule =
        Project("module-logger", file("modules/logger"))
            .settings(moduleSettings: _*)
            .settings(libraryDependencies := loggerKit)
            .dependsOn(coreModule, testModule % "test->test")

    lazy val servletModule =
        Project("module-servlet", file("modules/servlet"))
            .settings(moduleSettings: _*)
            .settings(libraryDependencies := List(Provided.servlet))
            .dependsOn(loggerModule, testModule % "test->test")

    lazy val testModule =
        Project("module-test", file("modules/test"))
            .settings(libraryDependencies ++= testKit)
            .settings(moduleSettings: _*)
}


// ### Settings -----------------------------------------------------------------------------------

trait Settings {

    import Licenses._

    lazy val javaHome =
        file(Option(System.getenv("JAVA6_HOME")).getOrElse(System.getenv("JAVA_HOME")))

    lazy val buildSettings = Seq(
        organization := "com.crashnote",
        version := "0.2.1",

        startYear := Some(2011),
        licenses +=("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

        organizationName := "101 Loops",
        organizationHomepage := Some(url("http://www.101loops.com"))
    )

    import LicenseKeys._
    lazy val baseSettings =
        Defaults.defaultSettings ++ buildSettings ++ Licenses.licenseSettings ++ Seq(
            crossPaths := false,
            scalaVersion := "2.9.2",

            license := apache2("Copyright (C) 2011 - 101loops.com <dev@101loops.com>"),

            resolvers += "spray repo" at "http://repo.spray.cc/",

            javacOptions += "-g:none",
            javacOptions ++= Seq("-source", "1.6", "-target", "1.6"),
            javacOptions ++= Seq("-bootclasspath", (javaHome / "lib" / "rt.jar").getAbsolutePath)
        )

    lazy val moduleSettings =
        baseSettings

    lazy val notifierSettings =
        baseSettings ++ Publish.settings


    def moduleSources(mods: String*) =
        baseDirectory(d => mods.map(d / ".." / "modules" / _ / "src" / "main" / "java"))
}


// ### Commands -----------------------------------------------------------------------------------

trait Commands {

    def intellij = Command.command("intellij") {
        state =>
            val extracted = Project.extract(state)
            //println("Current build: " + currentRef.)

            state
    }

}

// ### Dependencies -------------------------------------------------------------------------------

object Dependencies {

    import Dependency._

    val loggerKit =
        Seq(Provided.slf4j, Provided.log4j, Provided.logback)

    val testKit =
        Seq(Test.junit, Test.specs2, Test.mockito, Test.jetty, Test.akka, Test.sprayClient, Test.sprayServer)
}

object Dependency {

    object Provided {
        val slf4j = "org.slf4j" % "slf4j-api" % "1.6.4" % "provided"
        val log4j = "log4j" % "log4j" % "1.2.16" % "provided"
        val logback = "ch.qos.logback" % "logback-classic" % "1.0.0" % "provided"

        val servlet = "javax.servlet" % "servlet-api" % "2.5" % "provided"
        val appengine = "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.0" % "provided"
    }

    object Test {
        val junit = "junit" % "junit" % "4.10" % "test"
        val specs2 = "org.specs2" % "specs2_2.9.1" % "1.9" % "test"
        val mockito = "org.mockito" % "mockito-all" % "1.9.0" % "test"

        val jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.5.1.v20110908" % "test"

        val akka = "se.scalablesolutions.akka" % "akka-actor" % "1.3.1" % "test"
        val sprayServer = "cc.spray" % "spray-server" % "0.9.0" % "test"
        val sprayClient = "cc.spray" % "spray-client" % "0.9.0" % "test"
    }

}


// ### DEPLOYING ----------------------------------------------------------------------------------

object Publish {

    lazy val settings =
        Seq(
            publishMavenStyle := true,
            pomIncludeRepository := {
                x => false
            },
            makePomConfiguration ~= {
                (mpc: MakePomConfiguration) =>
                    mpc.copy(configurations = Some(Seq(Provided)))
            },
            pomExtra :=
                <url>http://www.crashnote.com</url>

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