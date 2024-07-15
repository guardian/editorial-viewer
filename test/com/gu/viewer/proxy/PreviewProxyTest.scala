package com.gu.viewer.proxy

import lib.ConfigHelpers
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.must.Matchers
import play.api.mvc.Results._
import play.api.mvc.Session
import play.api.test.Helpers.GET

import scala.concurrent.ExecutionContext

class PreviewProxyTest extends AsyncFunSuite with Matchers with MockitoSugar with ConfigHelpers {

  val supportedServicePath = "item/42"
  val requestHost = "example.com"

  val createPreviewProxy: PreviewProxy = {
    val supportedProxyUrl = s"https://${devAppConfig.previewHost}/${supportedServicePath}"
    val mockProxyClient = makeProxyClient {
      case (GET, userUrl) => Action {
        userUrl match {
          case supportedProxyUrl => Ok
          case _ => NotFound
        }
      }
    }

    new PreviewProxy(
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

  def createRequest(path: String): PreviewProxyRequest = {
    new PreviewProxyRequest(
      servicePath = path,
      requestHost = requestHost,
      requestUri = s"https://${requestHost}/${path}",
      requestQueryString = Map.empty,
      session = createSession
    )
  }

  test("can construct login url") {
    val instance = createPreviewProxy
    instance.previewLoginUrl must be("https://preview.dev-host/login")
  }

  test("will proxy requests to the configured preview host") {
    val instance = createPreviewProxy
    val request = createRequest(supportedServicePath)

    instance.proxy(request) map {
      result => assert(result.header.status === 200)
    }
  }
}
