package com.redhat.samples.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class BasicJavaTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in").to("mock:out");
            }
        };
    }

    @Test
    public void hello() throws Exception {
        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(1);
        out.expectedBodiesReceived("Hello!");

        template.sendBody("direct:in", "Hello!");
        out.assertIsSatisfied();
    }

}
