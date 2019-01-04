package com.gu.viewer.aws

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.auth.{AWSCredentialsProviderChain, InstanceProfileCredentialsProvider}
import com.amazonaws.regions.Regions
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder
import com.amazonaws.services.ec2.model.{DescribeTagsRequest, Filter}
import com.amazonaws.services.simpleemail._
import com.amazonaws.util.EC2MetadataUtils

import scala.collection.JavaConverters._

object AWS extends AwsInstanceTags {

  val region = Regions.EU_WEST_1
  val credentials = new AWSCredentialsProviderChain(
    new ProfileCredentialsProvider("composer"),
    InstanceProfileCredentialsProvider.getInstance()
  )

  val EC2Client = AmazonEC2ClientBuilder.standard().withRegion(region).withCredentials(credentials).build()
  val SESClient = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(region).withCredentials(credentials).build()
}

trait AwsInstanceTags {
  val instanceId = Option(EC2MetadataUtils.getInstanceId)

  def readTag(tagName: String): Option[String] = {
    instanceId.flatMap { id =>
      val tagsResult = AWS.EC2Client.describeTags(
        new DescribeTagsRequest().withFilters(
          new Filter("resource-type").withValues("instance"),
          new Filter("resource-id").withValues(id),
          new Filter("key").withValues(tagName)
        )
      )
      tagsResult.getTags.asScala.find(_.getKey == tagName).map(_.getValue)
    }
  }
}
