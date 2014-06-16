package com.redhat.samples.camel

import java.io.File
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.component.mock.MockEndpoint
import org.hamcrest.Matchers._
import org.junit.Assert._
import org.junit.Test

class OnExceptionTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure {
      // format: OFF
      onException(classOf[Exception])
        .handled(true)
        .log("=" * 80)
        .log("ERROR: ${exception.message}")
        .log("=" * 80)
        .transform().simple("ERROR: ${exception.message}")

      from("direct:in")
        .choice()
          .when(body().contains("Error"))
            .throwException(new Exception("xxxxx"))
          .otherwise()
            .transform().simple("Hello, ${body}!")
      // format: ON
    }
  }

  @Test
  def ok {
    val reply = template.requestBody("direct:in", "Test")
    assertThat(reply.toString, is("Hello, Test!"))
  }

  @Test
  def error {
    val reply = template.requestBody("direct:in", "Error")
    assertThat(reply.toString, is("ERROR: xxxxx"))
  }

}