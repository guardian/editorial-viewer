libraryDependencies += "org.vafer" % "jdeb" % "1.7" artifacts (Artifact("jdeb", "jar", "jar"))
// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0")
// addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.4.4")    -- TO DO - restore packaging plugins when app upgraded
// addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3") -- TO DO - restore packaging plugins when app upgraded
// addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")   -- TO DO - restore packaging plugins when app upgraded

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
