package com.gu.viewer.controllers

import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser
import com.gu.viewer.config.Configuration

trait PanDomainAuthActions extends AuthActions {

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override def cacheValidation = true

  override def authCallbackUrl: String = Configuration.pandaAuthCallback

  override lazy val domain: String = Configuration.pandaDomain

  override lazy val system: String = "viewer"
}
