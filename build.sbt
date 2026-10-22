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

scalaVersion := "2.13.18"

val awsVersion = "2.57.0"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "s3" % awsVersion,
  "software.amazon.awssdk" % "ec2" % awsVersion,
  "software.amazon.awssdk" % "ses" % awsVersion,
  "software.amazon.awssdk" % "imds" % awsVersion,
  "com.gu" %% "pan-domain-auth-play_3-0" % "22.2.0",
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
