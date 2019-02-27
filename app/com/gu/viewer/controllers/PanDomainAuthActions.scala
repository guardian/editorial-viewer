package com.gu.viewer.controllers

import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser
import com.gu.viewer.config.AppConfig

trait PanDomainAuthActions extends AuthActions {

  def config: AppConfig

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith "@guardian.co.uk") && authedUser.multiFactor
  }

  override def cacheValidation = true

  override def authCallbackUrl: String = config.pandaAuthCallback

}
