import com.amazonaws.auth._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2ClientBuilder}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.simpleemail.{AmazonSimpleEmailService, AmazonSimpleEmailServiceClientBuilder}
import com.gu.pandomainauth.PanDomainAuthSettingsRefresher
import com.gu.viewer.aws.AwsInstanceTags
import com.gu.viewer.config.AppConfig
import com.gu.viewer.controllers.{Application, Email, Management, Proxy}
import com.gu.viewer.logging.RequestLoggingFilter
import com.gu.viewer.proxy.{LiveProxy, PreviewProxy, ProxyClient}
import controllers.AssetsComponents
import play.api.{BuiltInComponentsFromContext, Mode}
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.csrf.CSRFComponents
import router.Routes

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with AssetsComponents
    with CSRFComponents
    with AhcWSComponents {

  def creds: AWSCredentialsProvider = new AWSCredentialsProviderChain(
    new EnvironmentVariableCredentialsProvider,
    new SystemPropertiesCredentialsProvider,
    new ProfileCredentialsProvider("composer"),
    InstanceProfileCredentialsProvider.getInstance()
  )

  val region: Regions = Regions.EU_WEST_1

  val ec2Client: AmazonEC2 = AmazonEC2ClientBuilder.standard().withRegion(region).withCredentials(creds).build()
  val s3Client: AmazonS3 = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(creds).build()
  val emailClient: AmazonSimpleEmailService =
    AmazonSimpleEmailServiceClientBuilder.standard().withRegion(region).withCredentials(creds).build()

  val tags = new AwsInstanceTags(ec2Client)
  val config = new AppConfig(tags, context.initialConfiguration)

  val panDomainSettings: PanDomainAuthSettingsRefresher = new PanDomainAuthSettingsRefresher(
    domain = config.pandaDomain,
    system = "viewer",
    s3Client = s3Client,
    bucketName = config.pandaBucket,
    settingsFileKey = config.pandaSettingsFileKey
  )

  val requestLoggingFilter = new RequestLoggingFilter(materializer, panDomainSettings)
  override def httpFilters: Seq[EssentialFilter] = Seq(requestLoggingFilter, csrfFilter)

  val proxyClient = new ProxyClient(wsClient, config)
  val liveProxy = new LiveProxy(proxyClient, config)
  val previewProxy = new PreviewProxy(proxyClient, config)

  val applicationController = new Application(controllerComponents, config)
  val managementController = new Management(controllerComponents)
  val proxyController = new Proxy(controllerComponents, previewProxy, liveProxy)
  val emailController = new Email(controllerComponents, wsClient, emailClient, config, panDomainSettings)

  override def router: Router = new Routes(
    httpErrorHandler,
    applicationController,
    managementController,
    proxyController,
    emailController,
    assets
  )
}
