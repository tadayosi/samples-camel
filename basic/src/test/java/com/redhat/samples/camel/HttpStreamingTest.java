package com.redhat.samples.camel;

import com.redhat.samples.camel.helpers.StreamHelper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Test it with <code>-Xmx64m</code>.
 */
public class HttpStreamingTest extends CamelTestSupport {

    private final StreamHelper stream = new StreamHelper(1000 * 100); // 100 MB

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                // @formatter:off
                from("direct:http-to-jetty").routeId("http-to-jetty")
                    .process(stream::produce)
                    .to("http://localhost:9001?disableStreamCache=true")
                    .process(stream::consume)
                    .log("length = ${body}");
                from("jetty:http://localhost:9001?disableStreamCache=true").routeId("jetty-to-http")
                    .process(stream::consume)
                    .log("length = ${body}")
                    .to("mock:check-length")
                    .process(stream::produce);

                from("direct:http-to-undertow").routeId("http-to-undertow")
                    .process(stream::produce)
                    .to("http://localhost:9002?disableStreamCache=true")
                    .process(stream::consume)
                    .log("length = ${body}");
                from("undertow:http://localhost:9002?useStreaming=true").routeId("undertow-to-http")
                    .process(stream::consume)
                    .log("length = ${body}")
                    .to("mock:check-length")
                    .process(stream::produce);

                from("direct:http-to-rest-undertow").routeId("http-to-rest-undertow")
                    .process(stream::produce)
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .to("http://localhost:9003/rest/undertow/post?disableStreamCache=true")
                    .process(stream::consume)
                    .log("length = ${body}");
                restConfiguration()
                    .component("undertow")
                    .contextPath("rest")
                    .host("localhost")
                    .port(9003)
                    .endpointProperty("useStreaming", "true");
                rest("/undertow")
                    .post("/post").produces("text/plain")
                    .to("direct:rest-undertow-to-http");
                from("direct:rest-undertow-to-http").routeId("rest-undertow-to-http")
                    .process(stream::consume)
                    .log("length = ${body}")
                    .to("mock:check-length")
                    .process(stream::produce);

                from("direct:undertow-to-jetty").routeId("undertow-to-jetty")
                    .process(stream::produce)
                    .to("undertow:http://localhost:9004?useStreaming=true")
                    .process(stream::consume)
                    .log("length = ${body}");
                from("jetty:http://localhost:9004?disableStreamCache=true").routeId("jetty-to-undertow")
                    .process(stream::consume)
                    .log("length = ${body}")
                    .to("mock:check-length")
                    .process(stream::produce);

                from("direct:undertow-receive-only").routeId("undertow-receive-only")
                    .to("undertow:http://localhost:9005?useStreaming=true")
                    .process(stream::consume)
                    .log("length = ${body}");
                from("jetty:http://localhost:9005?disableStreamCache=true")
                    .process(stream::produce);
                // @formatter:on
            }
        };
    }

    private void doTest(String endpointUri) throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:check-length");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived(stream.length());

        long length = template.requestBody(endpointUri, null, Long.class);

        mock.assertIsSatisfied();
        assertEquals(stream.length(), length);
    }

    @Test
    public void httpToJetty() throws Exception {
        doTest("direct:http-to-jetty");
    }

    @Test
    public void httpToUndertow() throws Exception {
        doTest("direct:http-to-undertow");
    }

    @Test
    public void httpToRestUndertow() throws Exception {
        doTest("direct:http-to-rest-undertow");
    }

    @Test
    public void undertowToJetty() throws Exception {
        doTest("direct:undertow-to-jetty");
    }

    @Test
    public void undertowReceiveOnly() throws Exception {
        long length = template.requestBody("direct:undertow-receive-only",
            "test", Long.class);
        assertEquals(stream.length(), length);
    }

}
