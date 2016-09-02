package com.redhat.samples.camel

import javax.ws.rs.{GET, Path, PathParam}

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.JndiRegistry
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.cxf.feature.LoggingFeature
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.junit.Test
import org.slf4j.LoggerFactory

class CxfrsTest extends CamelTestSupport {

  private val logger = LoggerFactory.getLogger(classOf[CxfrsTest])

  val address: String = "http://localhost:9000/"

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      getContext.setTracing(true)
      getContext.getRegistry(classOf[JndiRegistry])
        .bind("rsServer", rsServer)

      //from("cxfrs:bean:rsServer?bindingStyle=SimpleConsumer")
      from("cxfrs:bean:rsServer?bindingStyle=SimpleConsumer&synchronous=true")
        .log("body: '${body}'")
        .transform(simple("Hello, ${body}!"))
        .to("mock:out")
    }
  }

  private def rsServer: JAXRSServerFactoryBean = {
    val factory: JAXRSServerFactoryBean = new JAXRSServerFactoryBean
    factory.setResourceClasses(classOf[GreetingResource])
    factory.setAddress(address)
    factory.getFeatures.add(new LoggingFeature)
    //factory.setProvider(new JacksonJsonProvider)
    return factory
  }

  @Test
  def hello: Unit = {
    val out = getMockEndpoint("mock:out")
    out.expectedMessageCount(1)
    out.expectedBodiesReceived("Hello, Kayoko Ann Patterson!")

    try {
      template.sendBody("http://localhost:9000/greeting/hello/Kayoko%20Ann%20Patterson", null)
    } catch {
      case e: Throwable => logger.error(e.getMessage)
    }
    out.assertIsSatisfied
  }

}

@Path("/greeting")
trait GreetingResource {
  @GET
  @Path("/hello/{name}")
  def hello(@PathParam("name") name: String): String
}
