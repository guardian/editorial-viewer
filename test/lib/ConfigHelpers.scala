package lib

import com.gu.viewer.aws.AwsInstanceTags
import com.gu.viewer.config.AppConfig
import org.scalatest.matchers.must.Matchers
import org.mockito.MockitoSugar
import play.api.Configuration

trait ConfigHelpers extends Matchers with MockitoSugar   {
  private val devConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "DEV",
    "previewHost.DEV" -> "preview.dev-host",
    "liveHost.DEV" -> "live.dev-host",
    "composerReturnUri.DEV" -> "bar"
  )

  private val prodConfigValues: Map[String, Any] = Map.apply(
    "Stage" -> "PROD",
    "previewHost.DEV" -> "preview.dev-host",
    "liveHost.DEV" -> "live.dev-host",
    "composerReturnUri.DEV" -> "bar",
    "previewHost.PROD" -> "bar",
    "liveHost.PROD" -> "bar",
    "composerReturnUri.PROD" -> "bar"
  )

 private def mockTags(configValues: Map[String, Any]): AwsInstanceTags = {
    val mockTags: AwsInstanceTags = mock[AwsInstanceTags]
    when(mockTags.readTag("Stage")).thenReturn(configValues.get("Stage") match {
      case Some(value: String) => Some(value)
      case _ => None
    })
    when(mockTags.readTag("App")).thenReturn(None)
    mockTags
  }

  def prodAppConfig:AppConfig = {
    new AppConfig(mockTags(prodConfigValues), Configuration.from(prodConfigValues))
  }

  def devAppConfig:AppConfig = {
    new AppConfig(mockTags(devConfigValues),Configuration.from(devConfigValues))
  }
}
