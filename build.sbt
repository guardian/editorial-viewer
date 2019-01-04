name := "viewer"

version := "0.1-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .enablePlugins(RiffRaffArtifact)
  .enablePlugins(JDebPackaging)
  .enablePlugins(SystemdPlugin)
  .settings(
    javaOptions in Universal ++= Seq(
          "-Dpidfile.path=/dev/null"
     )
  )

scalaVersion := "2.12.8"

scalacOptions := Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Ypartial-unification"
)

val awsVersion = "1.11.475"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-ec2" % awsVersion,
  "com.amazonaws" % "aws-java-sdk-ses" % awsVersion,
  "com.gu" %% "pan-domain-auth-play_2-6" % "0.7.2",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "com.gu" % "kinesis-logback-appender" % "1.4.3",
  ws
)


// Config for packing app for deployment
packageName in Universal := normalizedName.value

riffRaffPackageName := s"editorial-tools:${name.value}"

riffRaffManifestProjectName := riffRaffPackageName.value

riffRaffUploadArtifactBucket := Option("riffraff-artifact")

riffRaffUploadManifestBucket := Option("riffraff-builds")

riffRaffArtifactResources := Seq(
  (packageBin in Debian).value -> s"${name.value}/${name.value}.deb",
  baseDirectory.value / "riff-raff.yaml" -> "riff-raff.yaml"
)

debianPackageDependencies := Seq("openjdk-8-jre-headless")
maintainer := "Digital CMS <digitalcms.dev@guardian.co.uk>"
packageSummary := "viewer"
packageDescription := """wrapper over the preview mode to give different platform previews"""
