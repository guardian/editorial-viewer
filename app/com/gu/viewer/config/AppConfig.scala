package com.gu.viewer.config

import com.gu.viewer.aws.AwsInstanceTags
import play.api.Configuration

class AppConfig(tags: AwsInstanceTags, config: Configuration) {

  private def getConfigString(key: String) =
    config.getOptional[String](key).getOrElse {
      sys.error(s"Config key required: $key")
    }

  lazy val app: String = tags.readTag("App").getOrElse("viewer")
  lazy val stage: String = tags.readTag("Stage").getOrElse("DEV")
  lazy val stack: String = tags.readTag("Stack").getOrElse("flexible")

  val logstashKinesisStream = config.getOptional[String]("aws.kinesis.logging.streamName")

  val previewHost = getConfigString(s"previewHost.$stage")
  val liveHost = getConfigString(s"liveHost.$stage")
  val previewHostForceHTTP = config.getOptional[Boolean](s"previewHostForceHTTP.$stage").getOrElse(false)
  val googleTrackingId = config.getOptional[String]("google.tracking.id").getOrElse("")
  val composerReturn = getConfigString(s"composerReturnUri.$stage")

  val pandaBucket = "pan-domain-auth-settings"
  val pandaSettingsFileKey = s"$pandaDomain.settings"

  def pandaDomain = {
    if (stage == "PROD") {
      "gutools.co.uk"
    } else if (stage == "CODE") {
      "code.dev-gutools.co.uk"
    } else {
      "local.dev-gutools.co.uk"
    }
  }

  def pandaAuthCallback = {
    if (stage == "PROD") {
      "https://viewer.gutools.co.uk/oauthCallback"
    } else if (stage == "CODE") {
      "https://viewer.code.dev-gutools.co.uk/oauthCallback"
    } else {
      "https://viewer.local.dev-gutools.co.uk/oauthCallback"
    }
  }
}
