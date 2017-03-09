package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.assertj.core.api.Assertions._
import org.junit.Test

class OnExceptionTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      // @formatter:off
      onException(classOf[Exception])
        .handled(true)
        .log("=" * 80)
        .log("ERROR: ${exception.message}")
        .log("=" * 80)
        .transform().simple("ERROR: ${exception.message}")

      from("direct:in").routeId(classOf[OnExceptionTest].getName)
        .choice()
          .when(body().contains("Error"))
            .throwException(new Exception("xxxxx"))
          .otherwise()
            .transform().simple("Hello, ${body}!")
      // @formatter:on
    }
  }

  @Test
  def ok: Unit = {
    val reply = template.requestBody("direct:in", "Test")
    assertThat(reply.toString).isEqualTo("Hello, Test!")
  }

  @Test
  def error: Unit = {
    val reply = template.requestBody("direct:in", "Error")
    assertThat(reply.toString).isEqualTo("ERROR: xxxxx")
  }

}
