package com.gu.viewer.logging

import akka.stream.Materializer
import net.logstash.logback.marker.Markers
import play.api.MarkerContext
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

import scala.collection.JavaConverters._

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

        val headersLength = requestHeader.headers.toMap.foldLeft(0)((acc, header) => acc + header.toString.length)

        if(headersLength > 8192) {
          val cookieString = requestHeader.cookies.foldLeft("")((acc, cookie) => acc + s"Name: ${cookie.name} Value: ${cookie.value.length} \n")
          val requestMarkers = Markers.appendEntries(
            Map("cookies" -> cookieString,
              "path" -> requestHeader.path,
              "domain" -> requestHeader.domain,
              "headersLength" -> headersLength)
            .asJava)

          log.warn(s"Request received with excessive header length ${headersLength}")(MarkerContext(requestMarkers))
        }

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
