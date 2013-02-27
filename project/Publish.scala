import sbt._
import sbt.Keys._
import xml.{Text, NodeSeq, Elem}

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

      pomPostProcess := {
        var displayName: String = null
        Rewrite.rewriter {
          // remove module dependencies
          case e@Elem(_, "dependency", _, _, child@_*) if (child.exists(_.text contains "crashnote")) =>
            NodeSeq.Empty

          // remove Scala dependencies
          case e@Elem(_, "dependency", _, _, child@_*) if (child.exists(_.text contains "scala")) =>
            NodeSeq.Empty

          // add scope "provided" for dependencies
          case e: Elem if e.label == "dependency" =>
            e.copy(child = e.child ++ Seq(<scope>provided</scope>))

          // a) extract artifactId and convert to display name
          case e: Elem if e.label == "artifactId" && (e.text).contains("crashnote") =>
            displayName = (e.text).split("-").map(_.capitalize).mkString(" ") + " Agent"
            e

          // b) apply display name
          case e: Elem if e.label == "name" && displayName.toLowerCase.contains(e.text) =>
            e.copy(child = Text(displayName))
        }
      },

      makePomConfiguration ~= {
        (mpc: MakePomConfiguration) =>
          mpc.copy(configurations = Some(Seq(Compile, Provided)))
      },

      pomExtra :=
        <url>https://www.crashnote.com</url>

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
            <developerConnection>scm:git:git@github.com:crashnote/crashnote-java.git</developerConnection>
          </scm>

          <issueManagement>
            <system>github</system>
            <url>https://github.com/crashnote/crashnote-java/issues</url>
          </issueManagement>
    )

  object Rewrite {

    import xml.{NodeSeq, Node => XNode}
    import xml.transform.{RewriteRule, RuleTransformer}

    def rewriter(f: PartialFunction[XNode, NodeSeq]): RuleTransformer = new RuleTransformer(rule(f))

    def rule(f: PartialFunction[XNode, NodeSeq]): RewriteRule = new RewriteRule {
      override def transform(n: XNode) = if (f.isDefinedAt(n)) f(n) else n
    }
  }

}