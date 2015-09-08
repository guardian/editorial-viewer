package com.gu.viewer.config

import play.api.Play.current
import com.gu.viewer.aws.AWS

object Configuration {

  // parsed config from application.conf
  private val config = play.api.Play.configuration

  private def getConfigString(key: String) =
    config.getString(key).getOrElse {
      sys.error(s"Config key required: $key")
    }

  lazy val app: String = AWS.readTag("App").getOrElse("viewer")

  lazy val stage: String = AWS.readTag("Stage") match {
    case Some(value) => value
    case None => "DEV" // default to DEV stage
  }

  lazy val stack: String = AWS.readTag("Stack").getOrElse("flexible")

  val previewHost = getConfigString(s"previewHost.$stage")
  val liveHost = getConfigString(s"liveHost.$stage")
  val mixpanel = getConfigString(s"mixpanel.$stage")
  val composerReturn = getConfigString(s"composerReturnUri.$stage")

  val logstashEnabled = config.getBoolean("logstash.enabled").getOrElse(false)
  val logstashDestination = config.getString("logstash.destination")
}
