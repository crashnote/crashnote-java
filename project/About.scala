import sbt._
import sbt.Keys._

object About extends Plugin {

  def aboutSettings =
    Seq(
      resourceGenerators in Compile
        <+= (sourceManaged in Compile, artifact, version) map {
        (out, artifact, version) =>
          val target = new File(out, "crashnote.about.properties")
          val name = artifact.name.replaceAllLiterally("crashnote", "cn")
          val data = "crashnote.about.name = " + name + "\n" + "crashnote.about.version = " + version
          IO.write(target, data, append = false)
          Seq(target)
      }
    )
}