package com.gu.viewer.proxy

import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.util.ByteString
import play.api.libs.ws.WSResponse


class ProxyResponse(response: WSResponse) {
  def allHeaders: Map[String, scala.collection.Seq[String]] = response.headers

  val status: Int = response.status

  def header(name: String): Option[String] =
    allHeaders.get(name).map(_.head)

  def bodyAsSource: Source[ByteString, _] = response.bodyAsSource

  // Use for logging only
  def bodyAsString: String = {
    response.body
  }

  override def toString =
    s"ProxyResponse($status, $allHeaders)"
}
