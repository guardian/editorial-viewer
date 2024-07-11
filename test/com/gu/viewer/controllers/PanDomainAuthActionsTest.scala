package com.gu.viewer.controllers

import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.viewer.config.AppConfig
import lib.ConfigHelpers
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import play.api.libs.ws.WSClient
import play.api.mvc.ControllerComponents

class PanDomainAuthActionsTest extends AnyFunSuite with Matchers with MockitoSugar with ConfigHelpers {

  class TestImplementation extends PanDomainAuthActions {
    override val wsClient: WSClient = mock[WSClient]
    override val config: AppConfig = devAppConfig
    override val panDomainSettings: PanDomainAuthSettingsRefresher = mock[PanDomainAuthSettingsRefresher]
    override val controllerComponents: ControllerComponents = mock[ControllerComponents]
  }
}
