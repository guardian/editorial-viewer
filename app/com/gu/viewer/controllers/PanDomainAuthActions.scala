package com.gu.viewer.controllers

import akka.actor.ActorSystem
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser
import com.gu.viewer.aws.AWS
import com.gu.viewer.config.Configuration
import play.api.mvc.BaseController

trait PanDomainAuthActions extends AuthActions { this: BaseController =>
  def actorSystem: ActorSystem
  def configuration: Configuration

  override def panDomainSettings: PanDomainAuthSettingsRefresher = new PanDomainAuthSettingsRefresher(
    configuration.pandaDomain,
    system = "viewer",
    actorSystem,
    awsCredentialsProvider = AWS.credentials
  )

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override def cacheValidation = true

  override def authCallbackUrl: String = configuration.pandaAuthCallback
}
