package com.gu.viewer.logging

import akka.stream.Materializer
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class RequestLoggingFilter(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with Loggable {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).flatMap { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      val responseInfo = result.header.status match {
        case 502 => Future.successful(s"502 bad gateway")
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
