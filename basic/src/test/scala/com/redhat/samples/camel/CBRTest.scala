package com.redhat.samples.camel

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.support.builder.xml.Namespaces
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

class CBRTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      val ns = new Namespaces("soap", "http://schemas.xmlsoap.org/soap/envelope/")
        .add("ns", "urn:samples-camel:basic:1.0")
      // @formatter:off
      from("direct:in")
        .choice
          .when.xpath("/soap:Envelope/soap:Body/ns:aaa", ns)
            .to("mock:a")
          .when.xpath("/soap:Envelope/soap:Body/ns:bbb", ns)
            .to("mock:b")
          .otherwise
            .to("mock:c")
      // @formatter:on
    }
  }

  private def test(uri: String, xml: String) = {
    val e = getMockEndpoint(uri)
    e.expectedMessageCount(1)
    e.expectedBodiesReceived(xml)

    template.sendBody("direct:in", xml)
    e.assertIsSatisfied()
  }

  @Test(timeout = 1000)
  def a: Unit = {
    val xml =
      <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="urn:samples-camel:basic:1.0">
        <soap:Header/>
        <soap:Body>
          <ns:aaa>
            <value>Test</value>
          </ns:aaa>
        </soap:Body>
      </soap:Envelope>
    test("mock:a", xml.toString)
  }

  @Test(timeout = 1000)
  def b: Unit = {
    val xml =
      <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="urn:samples-camel:basic:1.0">
        <soap:Header/>
        <soap:Body>
          <ns:bbb>
            <value>Test</value>
          </ns:bbb>
        </soap:Body>
      </soap:Envelope>
    test("mock:b", xml.toString)
  }

  @Test(timeout = 1000)
  def c: Unit = {
    val xml =
      <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="urn:samples-camel:basic:1.0">
        <soap:Header/>
        <soap:Body>
          <ns:ccc>
            <value>Test</value>
          </ns:ccc>
        </soap:Body>
      </soap:Envelope>
    test("mock:c", xml.toString)
  }

}
