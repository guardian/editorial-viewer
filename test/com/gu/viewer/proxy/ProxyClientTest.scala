package com.gu.viewer.proxy

import lib.ConfigHelpers
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AsyncFunSuite
import play.api.libs.ws.{WSClient, WSRequest}

import scala.concurrent.ExecutionContext

class ProxyClientTest extends AsyncFunSuite with MockitoSugar  with ConfigHelpers {

  def testInstance:ProxyClient = {
    val ws = mock[WSClient]
    when(ws.url("foo")).thenReturn(null)
    new ProxyClient(ws, devAppConfig)(ExecutionContext.Implicits.global)
  }

  // TO DO - install dependency for mocking a WSClient
  // https://github.com/leanovate/play-mockws
//  test("can construct") {
//  val instance = testInstance
//
//    val result = instance.get(
//      destination ="foo",
//      cookies = Seq.empty,
//      headers = Seq.empty,
//      queryString = Seq.empty,
//      followRedirects = false)()
//
//    result must be(null)
//  }

}
