name := "viewer"

version := "0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(RiffRaffArtifact)

scalaVersion := "2.11.6"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.10.10",
  ws
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


packageName in Universal := normalizedName.value

riffRaffPackageType := (packageZipTarball in config("universal")).value

riffRaffArtifactResources ++= Seq(
  baseDirectory.value / "cloudformation" / "editorial-viewer.json" ->
    "packages/cloudformation/editorial-viewer.json"
)
