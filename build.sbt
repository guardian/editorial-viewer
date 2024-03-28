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
  .enablePlugins(JDebPackaging)
  .enablePlugins(SystemdPlugin)
  .settings(
    Universal / javaOptions ++= Seq(
      "-Dpidfile.path=/dev/null"
     )
  )

scalaVersion := "2.13.0"

val awsVersion = "1.12.129"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ec2" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ses" % awsVersion,
  "com.gu" %% "pan-domain-auth-play_2-8" % "1.0.6",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.2",
  ws,
  "com.google.guava" % "guava" % "27.0-jre"
)

 pipelineStages := Seq(digest, gzip)

// Config for packing app for deployment
Universal / packageName := normalizedName.value

debianPackageDependencies := Seq("java11-runtime-headless")
maintainer := "Digital CMS <digitalcms.dev@guardian.co.uk>"
packageSummary := "viewer"
packageDescription := """wrapper over the preview mode to give different platform previews"""

PlayKeys.devSettings += "play.server.pekko.max-header-size" -> "16k"

