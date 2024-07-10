package com.gu.viewer.config

import com.gu.viewer.aws.AwsInstanceTags
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.mockito.MockitoSugar
import play.api.Configuration

class AppConfigTest extends AnyFunSuite with Matchers with MockitoSugar {

  val devConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "DEV",
    "previewHost.DEV" -> "bar",
    "liveHost.DEV" -> "bar",
    "composerReturnUri.DEV" -> "bar"
  )



  test ("can construct with DEV stage") {

    var mockTags: AwsInstanceTags = mock[AwsInstanceTags]
    var devConfig: Configuration = Configuration.from(devConfigValues)

    when(mockTags.readTag("Stage")).thenReturn(None)
    when(mockTags.readTag("App")).thenReturn(None)

    val instance: AppConfig = new AppConfig(mockTags,devConfig)

    instance.app must be ("viewer")
    instance.pandaDomain must be ("local.dev-gutools.co.uk")
    instance.pandaAuthCallback must be ("https://viewer.local.dev-gutools.co.uk/oauthCallback")
  }
}

