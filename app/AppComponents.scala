import com.gu.pandomainauth.{PanDomainAuthSettingsRefresher, S3BucketLoader}
import com.gu.contentapi.client.{GuardianContentClient, IAMSigner}
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
import software.amazon.awssdk.auth.credentials._
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest

import java.net.URI

class AppComponents(context: Context)
  extends BuiltInComponentsFromContext(context)
    with AssetsComponents
    with CSRFComponents
    with AhcWSComponents {

  val creds: AwsCredentialsProvider = AwsCredentialsProviderChain.of(
    EnvironmentVariableCredentialsProvider.create(),
    SystemPropertyCredentialsProvider.create(),
    ProfileCredentialsProvider.create("composer"),
    InstanceProfileCredentialsProvider.create()
  )

  val region: Region = Region.EU_WEST_1

  val ec2Client: Ec2Client = Ec2Client.builder().region(region).credentialsProvider(creds).build()
  val s3Client: S3Client = S3Client.builder().region(region).credentialsProvider(creds).build()
  val emailClient: SesClient = SesClient.builder().region(region).credentialsProvider(creds).build()

  val tags = new AwsInstanceTags(ec2Client)
  val config = new AppConfig(tags, context.initialConfiguration)

  val panDomainSettings: PanDomainAuthSettingsRefresher = PanDomainAuthSettingsRefresher(
    domain = config.pandaDomain,
    system = "viewer",
    S3BucketLoader.forAwsSdkV2(s3Client, "pan-domain-auth-settings")
  )

  val requestLoggingFilter = new RequestLoggingFilter(materializer, panDomainSettings)
  override def httpFilters: Seq[EssentialFilter] = Seq(requestLoggingFilter, csrfFilter)

  val proxyClient = new ProxyClient(wsClient, config)
  val liveProxy = new LiveProxy(proxyClient, config)
  val previewProxy = new PreviewProxy(proxyClient, config)

  val contentApiClient = for {
    contentApiKey <- config.contentApiKey
    previewContentApiUrl <- config.previewContentApiUrl
    previewContentApiRole <- config.previewContentApiRole
  } yield {
    val stsClient = StsClient.builder()
      .region(region)
      .credentialsProvider(creds)
      .build()

    val assumeRoleRequest = AssumeRoleRequest.builder()
      .roleSessionName("viewer-capi")
      .roleArn(previewContentApiRole)
      .build()

    val previewCredentials = AwsCredentialsProviderChain.builder()
      .credentialsProviders(
        ProfileCredentialsProvider.builder().profileName("capi").build(),
        StsAssumeRoleCredentialsProvider.builder()
          .stsClient(stsClient)
          .refreshRequest(assumeRoleRequest)
          .build()
      )
      .build()

    val signer = new IAMSigner(previewCredentials, region.id())

    val client = new GuardianContentClient(contentApiKey) {
      override val targetUrl: String = previewContentApiUrl

      override def get(url: String, headers: Map[String, String])(implicit
          context: scala.concurrent.ExecutionContext
      ): scala.concurrent.Future[com.gu.contentapi.client.model.HttpResponse] = {
        val signedHeaders = signer.addIAMHeaders(headers, URI.create(url))
        super.get(url, signedHeaders)(context)
      }
    }
    (client, stsClient)
  }

  contentApiClient.foreach { case (client, stsClient) =>
    applicationLifecycle.addStopHook(() => {
      client.shutdown()
      stsClient.close()
      scala.concurrent.Future.successful(())
    })
  }

  val applicationController = new Application(controllerComponents, wsClient, config, panDomainSettings)
  val managementController = new Management(controllerComponents)
  val proxyController = new Proxy(controllerComponents, wsClient, previewProxy, liveProxy, config, panDomainSettings)
  val emailController = new Email(controllerComponents, wsClient, emailClient, contentApiClient.map(_._1), config, panDomainSettings)

  override def router: Router = new Routes(
    httpErrorHandler,
    applicationController,
    managementController,
    proxyController,
    emailController,
    assets
  )
}
