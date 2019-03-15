package com.gu.viewer.proxy

import play.api.libs.iteratee.{Iteratee, Enumeratee, Enumerator}
import play.api.libs.ws.WSResponseHeaders
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, SECONDS}
import scala.util.control.NonFatal


case class ProxyResponse(private val headers: WSResponseHeaders, body: Enumerator[Array[Byte]]) {
  lazy val allHeaders = headers.headers

  lazy val status = headers.status

  def header(name: String) =
    allHeaders.get(name).map(_.head)

  // Use for logging only
  def bodyAsString = {
    val bytesToString: Enumeratee[ Array[Byte], String ] = Enumeratee.map[Array[Byte]]{ bytes => new String(bytes) }
    val consume: Iteratee[String,String] = Iteratee.consume[String]()

    try {
      Await.result(body |>>> bytesToString &>> consume, Duration(1, SECONDS))
    } catch {
      case NonFatal(err) => "<timeout resolving body>"
    }
  }

  override def toString =
    s"ProxyResponse($status, $allHeaders)"
}
