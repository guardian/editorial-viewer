package com.gu.viewer

import com.gu.viewer.config.Configuration
import _root_.controllers.AssetsComponents
import controllers.{Application, Email, Management, Proxy}
import com.gu.viewer.logging.RequestLoggingFilter
import com.gu.viewer.proxy.{LiveProxy, PreviewProxy, ProxyClient}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.filters.HttpFiltersComponents
import play.filters.https.RedirectHttpsComponents
import router.Routes

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with HttpFiltersComponents with AssetsComponents with RedirectHttpsComponents with AhcWSComponents {

  val appConfig = new Configuration(configuration)

  val proxyClient = new ProxyClient(appConfig.stage, null)
  val preview = new PreviewProxy(appConfig.previewHost, appConfig.previewHostForceHTTP, proxyClient)
  val live = new LiveProxy(appConfig.liveHost, proxyClient)

  val applicationController = new Application(appConfig, controllerComponents)
  val emailController = new Email(appConfig, actorSystem, null, controllerComponents)
  val managementController = new Management(controllerComponents)
  val proxyController = new Proxy(preview, live, controllerComponents)

  override lazy val httpFilters: Seq[EssentialFilter] = super.httpFilters.filterNot(_ == allowedHostsFilter) ++ Seq(new RequestLoggingFilter())
  override val router = new Routes(httpErrorHandler, applicationController, managementController, proxyController, emailController, null)
}
