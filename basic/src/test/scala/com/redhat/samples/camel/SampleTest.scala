package com.redhat.samples.camel

import java.io.File
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.component.mock.MockEndpoint
import org.hamcrest.Matchers._
import org.junit.Assert._
import org.junit.Test

class SampleTest extends CamelTestSupport {

    override def createRouteBuilder = new RouteBuilder {
        override def configure {
            from("direct:in").to("mock:out")
        }
    }

    @Test
    def hello {
        val out = getMockEndpoint("mock:out")
        out.expectedMessageCount(1)
        out.expectedBodiesReceived("Hello!")

        template.sendBody("direct:in", "Hello!")
        out.assertIsSatisfied()
    }

}