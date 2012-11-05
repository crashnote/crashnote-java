logLevel := Level.Info

// Repository: Typesafe
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// Plugin: IntelliJ
addSbtPlugin("com.github.mpeltonen" %% "sbt-idea" % "1.1.0-M2-TYPESAFE")

// Plugin: Eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.0")

