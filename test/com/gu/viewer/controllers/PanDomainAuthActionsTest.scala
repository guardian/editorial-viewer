package com.gu.viewer.controllers

import com.gu.viewer.aws.AwsInstanceTags
import com.gu.viewer.config.AppConfig
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher

import org.scalatest.funsuite.AnyFunSuite;
import org.mockito.MockitoSugar;
import play.api.Configuration;
import play.api.libs.ws.WSClient;
import play.api.mvc.ControllerComponents;
import org.scalatest.matchers.must.Matchers;

class PanDomainAuthActionsTest extends AnyFunSuite with Matchers with MockitoSugar {

  val devConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "DEV",
    "previewHost.DEV" -> "bar",
    "liveHost.DEV" -> "bar",
    "composerReturnUri.DEV" -> "bar"
  )

  var mockTags: AwsInstanceTags = mock[AwsInstanceTags]
  var devConfig: Configuration = Configuration.from(devConfigValues)


  class TestImplementation extends PanDomainAuthActions {
    override val wsClient: WSClient = mock[WSClient]
    override val config: AppConfig = new AppConfig(mockTags, devConfig)
    override val panDomainSettings: PanDomainAuthSettingsRefresher = mock[PanDomainAuthSettingsRefresher]
    override val controllerComponents: ControllerComponents = mock[ControllerComponents]
  }

}
