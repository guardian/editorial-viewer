package com.gu.viewer.Logging

import play.api.Logger
import play.api.libs.iteratee.{Iteratee, Enumeratee}
import play.api.mvc._
import scala.concurrent.Future
import scala.util.Success
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.util.control.NonFatal

class RequestLoggingFilter extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).flatMap { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      def bodyAsString(): Future[String] = {
        val bytesToString: Enumeratee[ Array[Byte], String ] = Enumeratee.map[Array[Byte]]{ bytes => new String(bytes) }
        val consume: Iteratee[String,String] = Iteratee.consume[String]()

        result.body |>>> bytesToString &>> consume
      }

      val responseInfo = result.header.status match {
        case 502 => bodyAsString().map { s => s"502 bad gateway: $s" }
        case 303 => Future.successful(s"303 redirect to: ${result.header.headers.getOrElse("Location", "<unknown>")}")
        case status => Future.successful(status.toString)

      }

      def doLog(info: String) = {
        Logger.info(s"${requestHeader.method} ${requestHeader.uri} " +
          s"took ${requestTime}ms and returned $info")

        result.withHeaders("Request-Time" -> requestTime.toString)
      }

      responseInfo.map { info =>
        doLog(info)

      } recover {
        case NonFatal(ex) => {
          Logger.warn("Error retrieving response body for logging", ex)
          doLog("<unknown body>")
        }
      }

    }
  }
}