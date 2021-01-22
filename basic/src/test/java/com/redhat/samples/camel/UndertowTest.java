package com.redhat.samples.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Will be fixed in 3.8.0")
public class UndertowTest extends CamelTestSupport {

    private static final int PORT = 9101;

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                restConfiguration()
                    .component("undertow")
                    .contextPath("rest")
                    .host("localhost")
                    .port(PORT);
                rest("/undertow")
                    .get("/nullBody").produces("application/json")
                    .route()
                    .transform().constant(null)
                    .log("body = ${body}");
            }
        };
    }

    @Test
    public void contentTypeWithNullBody() {
        Exchange reply = template.send(
            String.format("http://localhost:%s/rest/undertow/nullBody", PORT),
            createExchangeWithBody(null));
        assertNull(reply.getMessage().getBody());
        assertEquals("application/json",
            reply.getMessage().getHeader(Exchange.CONTENT_TYPE));
    }
}
