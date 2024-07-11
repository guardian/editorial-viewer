package com.gu.viewer.proxy

import lib.ConfigHelpers
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext

class PreviewProxyTest extends AsyncFunSuite with Matchers with MockitoSugar with ConfigHelpers {

  val testInstance:PreviewProxy = {
    var mockProxyClient = mock[ProxyClient]
    new PreviewProxy (
     mockProxyClient,
     devAppConfig,
    )(ExecutionContext.Implicits.global)
  }

  test("can construct login url") {
    val instance = testInstance
    instance.previewLoginUrl must be ("https://preview.dev-host/login")
  }
}
