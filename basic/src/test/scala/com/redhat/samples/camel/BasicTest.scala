package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class BasicTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure {
      from("direct:in").to("mock:out")
    }
  }

  @Test
  def hello: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(1)
    out.expectedBodiesReceived("Hello!")

    template.sendBody("direct:in", "Hello!")
    out.assertIsSatisfied()
  }

}
