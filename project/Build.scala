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
      .aggregate(/*play2Agent,*/ servletAgent, appengineAgent, coreModule, loggerModule, webModule, testModule)


  // ### Agents ----------------------------------------------------------------------------------

  lazy val servletAgent =
    AgentProject("servlet", "Crashnote Servlet Agent",
      withProjects = Seq(webModule),
      withLibraries = loggerKit ++ List(servlet))
      .settings(description := "Reports exceptions from Java servlet apps to crashnote.com")

  lazy val appengineAgent =
    AgentProject("appengine", "Crashnote Appengine Agent",
      withProjects = Seq(servletAgent),
      withLibraries = loggerKit ++ List(servlet, appengine))
      .settings(description := "Reports exceptions from Java apps on Appengine to crashnote.com")

  /*
  lazy val play2Agent =
      AgentProject("play2", "Crashnote Play2 Agent",
          withProjects = Seq(webModule),
          withLibraries = loggerKit ++ List(play2))
          .settings(description := "Reports exceptions from play2 apps to crashnote.com")
  */


  // ### Modules --------------------------------------------------------------------------------

  // Internal

  lazy val coreModule =
    ModuleProject("core", withModules = Seq(jsonModule, configModule))

  lazy val loggerModule =
    ModuleProject("logger",
      withModules = Seq(coreModule), withLibraries = loggerKit)

  lazy val webModule =
    ModuleProject("web", withModules = Seq(loggerModule))

  // External

  lazy val jsonModule =
    ModuleProject("ext-json")

  lazy val configModule =
    ModuleProject("ext-config")
}