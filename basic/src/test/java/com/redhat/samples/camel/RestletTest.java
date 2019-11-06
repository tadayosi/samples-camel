package com.redhat.samples.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMethods;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

@Ignore("camel-restlet is removed in Camel 3")
public class RestletTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:hello")
                    .setHeader(Exchange.HTTP_METHOD, constant(HttpMethods.POST))
                    .log("body: ${body}")
                    .log("header: ${headers}")
                    .to("http://localhost:8080/hello");

                from("restlet:http://localhost:8080/hello?restletMethod=POST")
                    .streamCaching()
                    .log("body: ${body}")
                    .log("header: ${headers}")
                    .transform().simple("Hello, ${body}!");
            }
        };
    }

    @Test
    public void hello() {
        String result = template.requestBody(
            "direct:hello", "Test", String.class);
        assertThat(result, is("Hello, Test!"));
    }

}
