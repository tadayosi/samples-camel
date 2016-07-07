package com.redhat.samples.camel.spring

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.spring.CamelSpringTestSupport
import org.junit.Test
import org.springframework.context.support.ClassPathXmlApplicationContext

class BasicTest extends CamelSpringTestSupport {

  override def createApplicationContext =
    new ClassPathXmlApplicationContext("spring/basic-camel-context.xml")

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      from("direct:out").to("mock:out")
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
