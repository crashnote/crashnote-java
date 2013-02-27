import sbt._

object Dependencies {

  import Dependency._

  lazy val loggerKit =
    Seq(slf4j, log4j, logback)

  lazy val testKit =
    Seq(Test.junit, Test.specs2, Test.mockito, Test.commonsIO, Test.jetty)
}


object Dependency extends Global {

  val log4j = "log4j" % "log4j" % "1.2.16"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.0"
  val logback = "ch.qos.logback" % "logback-classic" % "1.0.0"

  val play2 = "play" % "play" % "2.1-RC3"
  val servlet = "javax.servlet" % "servlet-api" % "2.5"
  val appengine = "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.0"


  object Test {

    val junit = "junit" % "junit" % "4.10" % "test"
    val specs2 = "org.specs2" %% "specs2" % "1.13" % "test"
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % "test"
    val commonsIO = "commons-io" % "commons-io" % "2.3" % "test"
    val jetty = "org.eclipse.jetty" % "jetty-webapp" % "7.5.1.v20110908" % "test"
  }

}