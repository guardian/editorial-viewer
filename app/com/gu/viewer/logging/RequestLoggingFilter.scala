package com.gu.viewer.logging

import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class RequestLoggingFilter(materializer: Materializer)(implicit ec: ExecutionContext) extends Filter with Loggable {

  implicit val mat: Materializer = materializer

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).flatMap { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      def bodyAsString(): Future[String] = result.body.consumeData.map(_.utf8String)

      val responseInfo = result.header.status match {
        case 502 => bodyAsString().map { s => s"502 bad gateway: $s" }
        case 303 => Future.successful(s"303 redirect to: ${result.header.headers.getOrElse("Location", "<unknown>")}")
        case status => Future.successful(status.toString)

      }

      def doLog(info: String) = {
        log.info(s"${requestHeader.method} ${requestHeader.uri} " +
          s"took ${requestTime}ms and returned $info")

        result.withHeaders("Request-Time" -> requestTime.toString)
      }

      responseInfo.map { info =>
        doLog(info)

      } recover {
        case NonFatal(ex) => {
          log.warn("Error retrieving response body for logging", ex)
          doLog("<unknown body>")
        }
      }

    }
  }
}
