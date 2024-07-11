package com.gu.viewer.config

import lib.ConfigHelpers
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers
import org.mockito.MockitoSugar
import play.api.Configuration

class AppConfigTest extends AnyFunSuite with Matchers with MockitoSugar with ConfigHelpers {

  test ("can construct with DEV stage") {
    val instance: AppConfig = DevAppConfig
    instance.app must be ("viewer")
    instance.pandaDomain must be ("local.dev-gutools.co.uk")
    instance.pandaSettingsFileKey must be("local.dev-gutools.co.uk.settings")
    instance.pandaAuthCallback must be ("https://viewer.local.dev-gutools.co.uk/oauthCallback")
  }

  test("can construct with PROD stage") {
    val instance = ProdAppConfig
    instance.app must be("viewer")
    instance.pandaDomain must be("gutools.co.uk")
    instance.pandaSettingsFileKey must be("gutools.co.uk.settings")
    instance.pandaAuthCallback must be("https://viewer.gutools.co.uk/oauthCallback")
  }
}

