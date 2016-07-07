package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class QuartzTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      from("quartz:in?trigger.repeatInterval=100&trigger.repeatCount=9")
        .setBody(simple("Hello!"))
        .log("${body}")
        .to("mock:out")
    }
  }

  @Test
  def hello: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(10)

    Thread.sleep(2000)
    out.assertIsSatisfied()
  }

}
