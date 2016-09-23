package com.redhat.samples.camel;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class LambdaTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // @formatter:off
                from("direct:in")
                    .log("name = ${body}")
                    .process(e -> {
                        String message = String.format("Hello, %s!", e.getIn().getBody(String.class));
                        e.getIn().setBody(message);
                    })
                    .to("mock:out");
                // @formatter:on
            }
        };
    }

    @Test
    public void hello() throws Exception {
        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(1);
        out.expectedBodiesReceived("Hello, LambdaTest!");

        template.sendBody("direct:in", getClass().getSimpleName());
        out.assertIsSatisfied();
    }

}
