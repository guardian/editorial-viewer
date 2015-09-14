package com.gu.viewer.proxy

import play.api.mvc.{Cookie, Cookies, Session}

case class PreviewSession(sessionCookie: Option[String] = None,
                          authCookie: Option[String] = None,
                          returnUrl: Option[String] = None,
                          private val playSession: Session = Session()) {

  def asCookies: Seq[Cookie] = Seq(
    authCookie.map(Cookie(PreviewSession.COOKIE_PREVIEW_AUTH, _)),
    sessionCookie.map(Cookie(PreviewSession.COOKIE_PREVIEW_SESSION, _))
  ).flatten

  def asSessionPairs = {
    import PreviewSession._
    Seq(
      SESSION_KEY_PREVIEW_SESSION -> sessionCookie,
      SESSION_KEY_PREVIEW_AUTH -> authCookie,
      SESSION_KEY_RETURN_URL -> returnUrl)
  }

  def asPlaySession: Session =
    asSessionPairs.foldLeft(playSession) { (session: Session, p: (String, Option[String])) =>
      p match {
        case (key, Some(value)) => session + (key, value)
        case (key, None) => session - key
      }
    }

  def withPlaySessionFrom(other: PreviewSession) = copy(playSession = other.playSession)

  def withReturnUrl(returnUrl: Option[String]) = copy(returnUrl = returnUrl)

  def withoutReturnUrl =
    copy(returnUrl = None)

}

object PreviewSession {
  private val COOKIE_PREVIEW_SESSION = "PLAY_SESSION"
  private val COOKIE_PREVIEW_AUTH = "GU_PV_AUTH"

  private val SESSION_KEY_PREVIEW_SESSION = "preview-session"
  private val SESSION_KEY_PREVIEW_AUTH = "preview-auth"
  private val SESSION_KEY_RETURN_URL = "preview-auth-return-url"



  def apply(session: Session): PreviewSession = PreviewSession(
    session.get(SESSION_KEY_PREVIEW_SESSION),
    session.get(SESSION_KEY_PREVIEW_AUTH),
    session.get(SESSION_KEY_RETURN_URL),
    session
  )


  def fromResponseHeaders(response: ProxyResponse) = {

    def extractCookies(headerName: String, transformer: Option[String] => Cookies) =
      response.allHeaders.get(headerName).map {
        _.flatMap { h => transformer(Some(h)) }
      }

    val allCookies = (

      extractCookies("Set-Cookie", Cookies.fromSetCookieHeader) ++
      extractCookies("Cookie", Cookies.fromCookieHeader)

    ).flatten.groupBy(_.name).mapValues(_.head.value)

    PreviewSession(allCookies.get(COOKIE_PREVIEW_SESSION), allCookies.get(COOKIE_PREVIEW_AUTH))
  }

}
