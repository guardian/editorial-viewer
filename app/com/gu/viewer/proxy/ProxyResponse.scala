package com.gu.viewer.proxy

import play.api.libs.iteratee.Enumerator
import play.api.libs.ws.WSResponseHeaders


case class ProxyResponse(private val headers: WSResponseHeaders, body: Enumerator[Array[Byte]]) {
  lazy val allHeaders = headers.headers

  lazy val status = headers.status

  def header(name: String) =
    allHeaders.get(name).map(_.head)
}
