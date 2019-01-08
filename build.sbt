import com.typesafe.sbt.web.pipeline.Pipeline

name := "viewer"

version := "0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .enablePlugins(RiffRaffArtifact)
  .enablePlugins(JDebPackaging)
  .settings(
    javaOptions in Universal ++= Seq(
          "-Dpidfile.path=/dev/null"
     )
  )

scalaVersion := "2.11.6"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.11.86",
  "com.gu" %% "pan-domain-auth-play_2-4-0" % "0.5.1",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.5.1",
  "com.gu" % "kinesis-logback-appender" % "1.3.0",
  ws
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


// Front-end assets config
val bundle = taskKey[Pipeline.Stage]("JSPM bundle")

bundle := { mappings =>
  val log = streams.value.log
  val sourceDir = (resourceDirectory in Assets).value
  log.info("Running JSPM bundle")
  val cmd = Process("npm run bundlejs", baseDirectory.value) !< log
  if (cmd != 0) sys.error(s"Non-zero error code for `npm run bundlejs`: $cmd")
  mappings ++ ((sourceDir * "build.js*") pair relativeTo(sourceDir))
}

pipelineStages := Seq(bundle, digest, gzip)


// Config for packing app for deployment
packageName in Universal := normalizedName.value

riffRaffPackageName := s"editorial-tools:${name.value}"

riffRaffManifestProjectName := riffRaffPackageName.value

riffRaffBuildIdentifier := Option(System.getenv("BUILD_NUMBER")).getOrElse("DEV")

riffRaffUploadArtifactBucket := Option("riffraff-artifact")

riffRaffUploadManifestBucket := Option("riffraff-builds")

riffRaffArtifactResources := Seq(
  (packageBin in Debian).value -> s"${name.value}/${name.value}.deb",
  baseDirectory.value / "riff-raff.yaml" -> "riff-raff.yaml"
)

import com.typesafe.sbt.packager.archetypes.ServerLoader.Systemd
serverLoading in Debian := Systemd

debianPackageDependencies := Seq("openjdk-8-jre-headless")
maintainer := "Digital CMS <digitalcms.dev@guardian.co.uk>"
packageSummary := "viewer"
packageDescription := """wrapper over the preview mode to give different platform previews"""
