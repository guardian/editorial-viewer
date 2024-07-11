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

  val prodConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "PROD",
    "previewHost.DEV" -> "bar",
    "liveHost.DEV" -> "bar",
    "composerReturnUri.DEV" -> "bar",
    "previewHost.PROD" -> "bar",
    "liveHost.PROD" -> "bar",
    "composerReturnUri.PROD" -> "bar"
  )

  def mockTags(configValues: Map[String, Any]): AwsInstanceTags = {
    val mockTags: AwsInstanceTags = mock[AwsInstanceTags]
    when(mockTags.readTag("Stage")).thenReturn(configValues.get("Stage") match {
      case Some(value: String) => Some(value)
      case _ => None
    })
    when(mockTags.readTag("App")).thenReturn(None)
    mockTags
  }

  test ("can construct with DEV stage") {
    val instance: AppConfig = new AppConfig(mockTags(devConfigValues),Configuration.from(devConfigValues))
    instance.app must be ("viewer")
    instance.pandaDomain must be ("local.dev-gutools.co.uk")
    instance.pandaSettingsFileKey must be("local.dev-gutools.co.uk.settings")
    instance.pandaAuthCallback must be ("https://viewer.local.dev-gutools.co.uk/oauthCallback")
  }

  test("can construct with PROD stage") {
    val instance: AppConfig = new AppConfig(mockTags(prodConfigValues), Configuration.from(prodConfigValues))
    instance.app must be("viewer")
    instance.pandaDomain must be("gutools.co.uk")
    instance.pandaSettingsFileKey must be("gutools.co.uk.settings")
    instance.pandaAuthCallback must be("https://viewer.gutools.co.uk/oauthCallback")
  }
}

