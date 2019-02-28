libraryDependencies += "org.vafer" % "jdeb" % "1.6" artifacts (Artifact("jdeb", "jar", "jar"))
// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.21")
addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
addSbtPlugin("com.gu" % "sbt-riffraff-artifact" % "1.1.9")
addSbtCoursier