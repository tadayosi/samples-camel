package com.redhat.samples.camel.spring.boot;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("servlet")
            .contextPath("camel");

        rest()
            .get("/outMessageNullBody")
            .to("direct:outMessageNullBody");

        from("direct:outMessageNullBody")
            .log("body: ${body}")
            .process(e -> {
                e.getOut().setBody(null);
            })
            .log("body: ${body}")
        ;
    }
}
