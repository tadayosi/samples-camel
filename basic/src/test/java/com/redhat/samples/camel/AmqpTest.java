package com.redhat.samples.camel;

import com.redhat.samples.camel.helpers.EmbeddedBroker;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AmqpTest extends CamelTestSupport {

    private static final int BROKER_PORT = 5672;

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                setup(getContext().getComponent("amqp", AMQPComponent.class));

                from("amqp:queue:hello")
                    .log("body = ${body}")
                    .to("mock:out");
            }
        };
    }

    private void setup(AMQPComponent amqp) {
        JmsConnectionFactory factory = new JmsConnectionFactory();
        factory.setRemoteURI("amqp://localhost:" + BROKER_PORT);
        amqp.setConnectionFactory(factory);
    }

    private static EmbeddedBroker broker;

    @BeforeClass
    public static void startBroker() throws Exception {
        broker = new EmbeddedBroker(BROKER_PORT).start();
    }

    @AfterClass
    public static void stopBroker() throws Exception {
        if (broker != null) {
            broker.stop();
            broker = null;
        }
    }

    @Test
    public void hello() throws Exception {
        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(1);
        out.expectedBodiesReceived("Hello!");

        template.sendBody("amqp:queue:hello", "Hello!");
        out.assertIsSatisfied();
    }

}
