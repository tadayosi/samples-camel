package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.JndiRegistry
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class BeanTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      getContext.getRegistry(classOf[JndiRegistry])
        .bind("greeting", new GreetingBean)

      from("direct:in")
        .bean("greeting", "hello")
        .log("body: '${body}'")
        .to("mock:out")
    }
  }

  class GreetingBean {
    def hello(name: String): String = s"Hello, $name!"
  }

  @Test
  def hello: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(1)
    out.expectedBodiesReceived("Hello, Test!")

    template.sendBody("direct:in", "Test")
    out.assertIsSatisfied
  }

}
