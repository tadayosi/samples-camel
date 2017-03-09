package com.redhat.samples.camel.blueprint

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport
import org.junit.Test

class TemplateTest extends CamelBlueprintTestSupport {

  override def getBlueprintDescriptor = "META-INF/blueprint/template-camel-context.xml"

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
