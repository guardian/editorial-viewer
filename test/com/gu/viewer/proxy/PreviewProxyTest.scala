package com.gu.viewer.proxy

import com.gu.viewer.aws.AwsInstanceTags
import com.gu.viewer.config.AppConfig
import org.mockito.ArgumentMatchersSugar.any
import org.mockito.MockitoSugar
import org.mockito.MockitoSugar.mock
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.must.Matchers
import play.api.Configuration
import play.api.mvc.{Cookie, Session}
import play.api.mvc.request.RequestAttrKey.Session

import scala.concurrent.{ExecutionContext, Future}

class PreviewProxyTest extends AsyncFunSuite with Matchers with MockitoSugar {

  val devConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "DEV",
    "previewHost.DEV" -> "preview.my-host",
    "liveHost.DEV" -> "live.my-host",
    "composerReturnUri.DEV" -> "bar"
  )


  val testInstance:PreviewProxy = {
    var mockTags: AwsInstanceTags = mock[AwsInstanceTags]
    var devConfig: Configuration = Configuration.from(devConfigValues)
    when(mockTags.readTag("Stage")).thenReturn(None)
    when(mockTags.readTag("App")).thenReturn(None)

    var mockProxyClient = mock[ProxyClient]


   new PreviewProxy (
     mockProxyClient,
     new AppConfig(mockTags, devConfig)
    )(ExecutionContext.Implicits.global)

  }

  test("can construct login url") {
    val instance = testInstance
    instance.previewLoginUrl must be ("https://preview.my-host/login")
  }


}
