import com.typesafe.sbt.web.pipeline.Pipeline
import scala.sys.process.Process
import Path.relativeTo

name := "viewer"

version := "0.1-SNAPSHOT"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .enablePlugins(RiffRaffArtifact)
  .enablePlugins(JDebPackaging)
  .enablePlugins(SystemdPlugin)
  .settings(
    Universal / javaOptions ++= Seq(
      "-Dpidfile.path=/dev/null"
     )
  )

scalaVersion := "2.12.16"

val awsVersion = "1.12.129"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ec2" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ses" % awsVersion,
  "com.gu" %% "pan-domain-auth-play_2-8" % "1.0.6",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.5.1",
  "com.gu" % "kinesis-logback-appender" % "1.3.0",
  ws,
  "com.typesafe.play" %% "play-iteratees" % "2.6.1",
  "com.google.guava" % "guava" % "27.0-jre"
)

val jacksonVersion = "2.11.4"

//Necessary to override jackson-databind versions due to AWS and Play incompatibility
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion

// Front-end assets config
val bundle = taskKey[Pipeline.Stage]("JSPM bundle")

bundle := { mappings =>
  val log = sLog.value
  val sourceDir = (Assets / resourceDirectory).value
  log.info("Running JSPM bundle")
  val cmd = Process("npm run bundlejs", baseDirectory.value) !< log
  if (cmd != 0) sys.error(s"Non-zero error code for `npm run bundlejs`: $cmd")
  mappings ++ ((sourceDir * "build.js*") pair relativeTo(sourceDir))
}

pipelineStages := Seq(bundle, digest, gzip)

// Config for packing app for deployment
Universal / packageName := normalizedName.value

riffRaffPackageName := s"editorial-tools:${name.value}"

riffRaffManifestProjectName := riffRaffPackageName.value

riffRaffUploadArtifactBucket := Option("riffraff-artifact")

riffRaffUploadManifestBucket := Option("riffraff-builds")

riffRaffArtifactResources := Seq(
  (Debian / packageBin).value -> s"${name.value}/${name.value}.deb",
  baseDirectory.value / "riff-raff.yaml" -> "riff-raff.yaml"
)

debianPackageDependencies := Seq("openjdk-8-jre-headless")
maintainer := "Digital CMS <digitalcms.dev@guardian.co.uk>"
packageSummary := "viewer"
packageDescription := """wrapper over the preview mode to give different platform previews"""

PlayKeys.devSettings += "play.server.akka.max-header-value-length" -> "16k"
