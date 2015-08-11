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


  lazy val stage: String = AWS.readTag("Stage") match {
    case Some(value) => value
    case None => "DEV" // default to DEV stage
  }


  val previewHost = getConfigString(s"previewHost.$stage")
  val liveHost = getConfigString(s"liveHost.$stage")

}
