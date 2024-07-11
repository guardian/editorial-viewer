package com.gu.viewer.proxy

import lib.ConfigHelpers
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.must.Matchers
import play.api.mvc.Session

import scala.concurrent.{ExecutionContext, Future}

class PreviewProxyTest extends AsyncFunSuite with Matchers with MockitoSugar with ConfigHelpers {

  val testInstance:PreviewProxy = {
    // to do - proper mock client
    var mockProxyClient = mock[ProxyClient]

    new PreviewProxy (
     mockProxyClient,
     devAppConfig,
    )(ExecutionContext.Implicits.global)
  }

  def createSession: PreviewSession = {
    new PreviewSession(
      sessionCookie = None,
      authCookie = None,
      returnUrl = None,
      playSession = new Session
    )
  }
  def createRequest: PreviewProxyRequest  = {
    new PreviewProxyRequest(
      servicePath = "item/42",
      requestHost = "content.source.com",
      requestUri = "https://content.source.com/item/42",
      requestQueryString = Map.empty,
      session = createSession
    )
  }

  test("can construct login url") {
    val instance = testInstance
    instance.previewLoginUrl must be ("https://preview.dev-host/login")
  }

//  test("can proxy get request") {
//    val  instance = testInstance
//    val request = createRequest
//    instance.proxy(request) map {
//      result => assert(result.toString() === "foo")
//    }
//  }

}
