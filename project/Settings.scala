import sbt._
import sbt.Keys._

// ### Project ------------------------------------------------------------------------------------

trait Projects
  extends Settings with Global {

  self: Build =>

  import Dependencies._

  type ClasspathRef = ClasspathDep[ProjectReference]

  object ModuleProject {

    def apply(name: String,
              withModules: Seq[ClasspathRef] = Seq(), withLibraries: Seq[ModuleID] = Seq()) =
      Project("module-" + name, file("modules/" + name))
        .configs(UnitTest, FuncTest)
        .settings(moduleSettings: _*)
        .settings(libraryDependencies ++= withLibraries)
        .dependsOn((Seq(testModule % "test->test") ++ withModules): _*)
  }

  object AgentProject {

    def apply(name: String, displayName: String,
              withProjects: Seq[ClasspathRef], withLibraries: Seq[ModuleID] = Seq(), withSources: Seq[ClasspathRef] = Seq()) =
      Project(name, file(name))
        .configs(UnitTest, FuncTest)
        .settings(agentSettings: _*)
        .settings(libraryDependencies ++= withLibraries)
        .settings(normalizedName := "crashnote-" + name)
        .dependsOn((Seq(testModule % "test->test") ++ withProjects): _*)
  }

  lazy val testModule =
    Project("module-test", file("modules/test"))
      .settings(moduleSettings: _*)
      .settings(libraryDependencies ++= testKit)
}


// ### Settings -----------------------------------------------------------------------------------

trait Settings {

  self: Global with Build with Projects =>

  lazy val buildSettings = Seq(
    organization := "com.crashnote",
    version := "0.4.0",

    startYear := Some(2011),
    licenses +=("Apache 2", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),

    organizationName := "101 Loops",
    organizationHomepage := Some(url("http://www.101loops.com"))
  )

  lazy val testSettings = Seq(
    testOptions in Test := Seq(Tests.Filter((n: String) => unitFilter(n) || funcFilter(n))),
    testOptions in FuncTest := Seq(Tests.Filter(funcFilter)),
    testOptions in UnitTest := Seq(Tests.Filter(unitFilter))
  )

  lazy val baseSettings =
    Defaults.defaultSettings ++ buildSettings ++ testSettings ++ Licenses.licenseSettings ++ Seq(
      crossPaths := false,
      scalaVersion := scala,
      scalaBinaryVersion := scalaM,

      parallelExecution in Test := false,

      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",

      javacOptions ++= Seq("-source", "1.6", "-target", "1.6", "-Xlint"),
      javacOptions in doc := Seq("-source", "1.6"),
      //javacOptions ++= Seq("-bootclasspath", (javaDir / "jre" / "lib" / "rt.jar").getAbsolutePath)
      javaHome := Some(javaDir)
    )

  lazy val moduleSettings =
    baseSettings

  lazy val agentSettings =
    baseSettings ++ About.aboutSettings ++ Publish.settings ++ Seq(

      //managedClasspath in Compile <<=
      //    (managedClasspath in Compile) map {(cp) => cp}

      // add source and resource directories from dependencies to agent
      unmanagedSourceDirectories in Compile <++= (thisProject in Compile, loadedBuild) {
        (p, struct) => srcDirs(getAllDeps(p, struct))
      },
      unmanagedResourceDirectories in Compile <++= (thisProject in Compile, loadedBuild) {
        (p, struct) => resDirs(getAllDeps(p, struct))
      }
    )

  // Dependency-related

  private def getAllDeps(p: ResolvedProject, struct: Load.LoadedBuild): Seq[ResolvedProject] =
    Seq(p) ++ p.dependencies.flatMap(r => getAllDeps(Project.getProject(r.project, struct).get, struct))

  // Test-related

  lazy val FuncTest = config("func") extend (Test)
  lazy val UnitTest = config("unit") extend (Test)

  private def unitFilter(name: String): Boolean =
    name contains ".unit."

  private def funcFilter(name: String): Boolean =
    name contains ".func."

  // Source-related

  def srcDirs(projects: Seq[ResolvedProject]) =
    getDirs(projects, "java")

  def resDirs(projects: Seq[ResolvedProject]) =
    getDirs(projects, "resources")

  private def getDirs(projects: Seq[ResolvedProject], dirType: String) =
    projects.map(p => p.base / "src" / "main" / dirType)

  // Path-related

  lazy val javaDir =
    file(Option(System.getenv("JAVA6_HOME")).getOrElse(System.getenv("JAVA_HOME")))
}


// ### GLOBAL ----------------------------------------------------------------------------------

trait Global {

  val scalaM = "2.10"
  val scala = scalaM + ".0"
}