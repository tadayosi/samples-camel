package com.redhat.samples.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class InterceptTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Throwable.class)
                    .handled(true)
                    .maximumRedeliveries(3)
                    .log(LoggingLevel.ERROR, "error = ${exception.message}");
                intercept()
                    .log("***** Intercept **********")
                    .log("from = ${exchange.fromRouteId}")
                    .process(e -> {
                        interceptCount++;
                    });
                from("direct:in")
                    .to("seda:queue1")
                    .to("seda:queue2")
                    .to("mock:out");
                from("seda:queue1")
                    .to("mock:out");
                from("seda:queue2")
                    .to("seda:queue3");
                from("seda:queue3")
                    .to("mock:out");
            }
        };
    }

    @Override
    protected boolean useJmx() {
        return true;
    }

    private int interceptCount = 0;

    @Test
    public void hello() throws Exception {
        interceptCount = 0;

        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(3);
        out.expectedBodiesReceived("Hello!", "Hello!", "Hello!");

        template.sendBody("direct:in", "Hello!");
        out.assertIsSatisfied();
        assertThat(interceptCount, is(6));
    }

}
