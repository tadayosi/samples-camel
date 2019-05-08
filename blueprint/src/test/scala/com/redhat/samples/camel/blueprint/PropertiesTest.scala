package com.redhat.samples.camel.blueprint

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport
import org.apache.camel.{Exchange, Processor}
import org.junit.Test

class PropertiesTest extends CamelBlueprintTestSupport {

  override def getBlueprintDescriptor = "META-INF/blueprint/properties-camel-context.xml"

  override def createRouteBuilder = new RouteBuilder {
    override def configure {
      from("direct:in1")
        .transform().simple("{{greeting.hello}}, ${body}!")
        .to("mock:out")

      from("direct:in2")
        .process(new Processor {
          override def process(ex: Exchange): Unit = {
            val options = ex.getContext.getGlobalOptions
            options.put("greeting.goodbye", "Goodbye")
            ex.getContext.setGlobalOptions(options)
          }
        })
        .to("direct:in3")
      from("direct:in3")
        .transform().simple("${camelContext.globalOptions[greeting.goodbye]}, ${body}!")
        .to("mock:out")
    }
  }

  @Test
  def read: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(1)
    out.expectedBodiesReceived("Hello, Test!")

    template.sendBody("direct:in1", "Test")
    out.assertIsSatisfied()
  }

  @Test
  def setAndRead: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(1)
    out.expectedBodiesReceived("Goodbye, Test!")

    template.sendBody("direct:in2", "Test")
    out.assertIsSatisfied()
  }

}
