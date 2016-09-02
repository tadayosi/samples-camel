package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.{Exchange, Processor}
import org.hamcrest.Matchers._
import org.junit.Assert._
import org.junit.Test
import org.slf4j.LoggerFactory

class RedeliveryTest extends CamelTestSupport {

  private val logger = LoggerFactory.getLogger(classOf[RedeliveryTest])

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      onException(classOf[Exception])
        .maximumRedeliveries(2).redeliveryDelay(1000)

      from("direct:in")
        .process(new Processor {
          def process(ex: Exchange) {
            val count = Option(ex.getIn.getHeader(Exchange.REDELIVERY_COUNTER)).getOrElse(0)
              .asInstanceOf[Int]
            logger.info("{} time(s)", count)
            if (count < 2) throw new Exception
          }
        })
        .transform().simple("Hello, ${body}!")
    }
  }

  @Test
  def hello: Unit = {
    val reply = template.requestBody("direct:in", "Test")
    assertThat(reply.toString, is("Hello, Test!"))
  }

}
