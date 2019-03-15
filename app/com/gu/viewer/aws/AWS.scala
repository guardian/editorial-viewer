package com.gu.viewer.aws

import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.{Filter, DescribeTagsRequest}
import com.amazonaws.services.simpleemail._
import com.amazonaws.util.EC2MetadataUtils
import scala.collection.JavaConverters._

object AWS extends AwsInstanceTags {

  lazy val region = Region getRegion Regions.EU_WEST_1

  lazy val EC2Client = region.createClient(classOf[AmazonEC2Client], null, null)

  lazy val emailClient = new AmazonSimpleEmailServiceClient()
  emailClient.setRegion(region)
}

trait AwsInstanceTags {
  lazy val instanceId = Option(EC2MetadataUtils.getInstanceId)

  def readTag(tagName: String) = {
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
