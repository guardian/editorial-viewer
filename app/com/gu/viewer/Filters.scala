package com.gu.viewer

import javax.inject.Inject

import com.gu.viewer.logging.RequestLoggingFilter
import play.api.http.HttpFilters
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.concurrent.Future


class Filters @Inject() (logger: RequestLoggingFilter) extends HttpFilters {

  val filters = Seq(logger, HttpsRedirectFilter)

}

object HttpsRedirectFilter extends Filter {

  def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    //play uses lower case headers.
    requestHeader.headers.get("x-forwarded-proto") match {
      case Some("http") => {
          Future.successful(Results.Redirect("https://" + requestHeader.host + requestHeader.uri, 301))
      }
      case _ => nextFilter(requestHeader)
    }
  }
}
