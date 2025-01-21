package com.gu.viewer.logging

import org.apache.pekko.stream.Materializer
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.pandomainauth.model.AuthenticatedUser
import com.gu.pandomainauth.service.CookieUtils
import net.logstash.logback.marker.Markers
import play.api.MarkerContext
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.jdk.CollectionConverters._
import scala.util.Failure

class RequestLoggingFilter(materializer: Materializer, refresher: PanDomainAuthSettingsRefresher)(implicit ec: ExecutionContext) extends Filter with Loggable {

  implicit val mat: Materializer = materializer

  def readAuthenticatedUser(request: RequestHeader): Option[AuthenticatedUser] = readCookie(request) flatMap { cookie =>
    CookieUtils.parseCookieData(cookie.value, refresher.settings.signingAndVerification).toOption
  }

  def readCookie(request: RequestHeader): Option[Cookie] = request.cookies.get(refresher.settings.cookieSettings.cookieName)

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val maybeUser = readAuthenticatedUser(requestHeader)
    val userId = maybeUser.map(_.user.email).getOrElse("not logged in")
    val authenticatedIn: Set[String] = maybeUser.map(_.authenticatedIn).getOrElse(Set.empty)

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
        val headersLength = requestHeader.headers.headers.foldLeft(0){ case (acc, (headerKey, headerValue)) =>
          // calculate the size of the header, should be the key, plus value plus 4 (the colon, space, CR, LF)
          val headerSize = headerKey.length + headerValue.length + 4
          acc + headerSize
        }

        val markerContext = MarkerContext(Markers.appendEntries(Map(
          "userId" -> userId,
          "headersLength" -> headersLength,
          "includesLegacyPandaCookie" -> requestHeader.cookies.get("gutoolsAuth").isDefined,
          "authenticatedIn" -> authenticatedIn.toList.sorted.mkString(",")
        ).asJava))

        log.info(s"${requestHeader.method} ${requestHeader.uri} " +
          s"took ${requestTime}ms and returned $info")(markerContext)

        if(headersLength > 6144) {
          val cookieString = requestHeader.cookies.foldLeft("")((acc, cookie) => acc + s"Name: ${cookie.name} Value: ${cookie.value.length} \n")
          val requestMarkers = MarkerContext(Markers.appendEntries(Map(
            "cookies" -> cookieString,
            "path" -> requestHeader.path,
            "domain" -> requestHeader.domain,
            "headersLength" -> headersLength,
            "userId" -> userId
          ).asJava))

          log.warn(s"Request received with excessive header length ${headersLength}")(requestMarkers)
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
