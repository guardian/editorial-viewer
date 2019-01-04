package com.gu.viewer.config

import play.api.{Configuration => PlayConfiguration}
import com.gu.viewer.aws.AWS

class Configuration(underlying: PlayConfiguration) {
  val app: String = AWS.readTag("App").getOrElse("viewer")

  val stage: String = AWS.readTag("Stage").getOrElse("DEV")

  val stack: String = AWS.readTag("Stack").getOrElse("flexible")

  val previewHost = underlying.get[String](s"previewHost.$stage")
  val liveHost = underlying.get[String](s"liveHost.$stage")
  val previewHostForceHTTP = underlying.get[Option[Boolean]](s"previewHostForceHTTP.$stage").getOrElse(false)
  val googleTrackingId = underlying.get[Option[String]]("google.tracking.id").getOrElse("")
  val composerReturn = underlying.get[String](s"composerReturnUri.$stage")

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
