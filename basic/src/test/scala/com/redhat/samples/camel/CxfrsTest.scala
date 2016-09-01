package com.redhat.samples.camel

import javax.ws.rs.{GET, Path, PathParam}

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.JndiRegistry
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.cxf.feature.LoggingFeature
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.junit.Test

class CxfrsTest extends CamelTestSupport {

  val address: String = "http://localhost:9000/"

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      getContext.getRegistry(classOf[JndiRegistry])
        .bind("rsServer", rsServer)

      //from("cxfrs:bean:rsServer?bindingStyle=SimpleConsumer")
      from("cxfrs:bean:rsServer?bindingStyle=SimpleConsumer&synchronous=true")
        .log("body: '${body}'")
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
    //out.expectedBodiesReceived("Hello, Kayoko Ann Patterson!")
    out.expectedBodiesReceived("Kayoko Ann Patterson")

    try {
      template.sendBody("http://localhost:9000/greeting/hello/Kayoko%20Ann%20Patterson", null)
    } catch {
      case e: Throwable => println(e.getCause)
    }
    out.assertIsSatisfied
  }

}

@Path("/greeting")
trait GreetingResource {
  @GET
  @Path("/hello/{name}")
  def hello(@PathParam("name") name: String): String //= s"Hello, $name!"
}
