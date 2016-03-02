package com.gu.viewer.controllers

import play.api.mvc._
import javax.inject.Inject

class Login @Inject() extends Controller with PanDomainAuthActions {
  def oauthCallback = Action.async { implicit request =>
    processGoogleCallback()
  }
}
