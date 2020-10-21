package com.gu.viewer.logging

import akka.stream.Materializer
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.pandomainauth.model.AuthenticatedUser
import com.gu.pandomainauth.service.CookieUtils
import net.logstash.logback.marker.Markers
import play.api.MarkerContext
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.collection.JavaConverters._
import scala.util.Failure

class RequestLoggingFilter(materializer: Materializer, refresher: PanDomainAuthSettingsRefresher)(implicit ec: ExecutionContext) extends Filter with Loggable {

  implicit val mat: Materializer = materializer

  def readAuthenticatedUser(request: RequestHeader): Option[AuthenticatedUser] = readCookie(request) map { cookie =>
    CookieUtils.parseCookieData(cookie.value, refresher.settings.publicKey)
  }

  def readCookie(request: RequestHeader): Option[Cookie] = request.cookies.get(refresher.settings.cookieSettings.cookieName)

  val impactedUsers = Set(
    "jonathan.casson@guardian.co.uk",
    "tash.banks@guardian.co.uk",
    "tash.reith-banks@guardian.co.uk",
    "greg.whitmore@guardian.co.uk",
    "joanna.ruck@guardian.co.uk",
    "simon.hildrew@guardian.co.uk"
  )

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val userId = readAuthenticatedUser(requestHeader).map(_.user.email).getOrElse("not logged in")

    if (impactedUsers.contains(userId)) {
      val headerStrings = requestHeader.headers.headers.map{case (key, value) => s"$key: $value"}
      val requestMarkers = Markers.appendEntries(
        Map("headers" -> headerStrings.mkString("-H '","' -H '", "'"),
          "method" -> requestHeader.method,
          "uri" -> requestHeader.uri,
          "domain" -> requestHeader.domain,
          "userId" -> userId)
          .asJava)
      log.debug("Impacted user request details")(MarkerContext(requestMarkers))
    }

    val startTime = System.currentTimeMillis

    val eventualResult = nextFilter(requestHeader)
    eventualResult.onComplete{
      case Failure(exception) =>
        val requestMarkers = Markers.appendEntries(
          Map("method" -> requestHeader.method,
            "uri" -> requestHeader.uri,
            "domain" -> requestHeader.domain,
            "userId" -> userId)
            .asJava)
        log.error(s"Experienced error whilst serving request", exception  )(MarkerContext(requestMarkers))
      case _ => // do nothing on success
    }
    eventualResult.flatMap { result =>

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
          s"took ${requestTime}ms and returned $info")(MarkerContext(Markers.appendEntries(Map("userId" -> userId).asJava)))

        val headersLength = requestHeader.headers.toMap.foldLeft(0)((acc, header) => acc + header.toString.length)

        if(headersLength > 8192) {
          val cookieString = requestHeader.cookies.foldLeft("")((acc, cookie) => acc + s"Name: ${cookie.name} Value: ${cookie.value.length} \n")
          val requestMarkers = Markers.appendEntries(
            Map("cookies" -> cookieString,
              "path" -> requestHeader.path,
              "domain" -> requestHeader.domain,
              "headersLength" -> headersLength,
              "userId" -> userId)
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
