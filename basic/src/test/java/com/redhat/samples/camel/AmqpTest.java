package com.redhat.samples.camel;

import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.redhat.samples.camel.helpers.EmbeddedBroker;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.message.JmsMessage;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsMessageFacade;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmqpTest extends CamelTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AmqpTest.class);

    private static final int BROKER_PORT = 5672;

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                setup(context.getComponent("amqp", AMQPComponent.class));

                from("amqp:queue:hello")
                    .process(e -> {
                        Message message = e.getMessage();
                        LOG.info("========================================");
                        LOG.info("[Body] {}", message.getBody());
                        LOG.info("----------------------------------------");
                        LOG.info("[Headers]");
                        message.getHeaders().forEach((k, v) ->
                            LOG.info("  {} = {}", k, v));
                        LOG.info("========================================");
                    })
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
        out.message(0).header("aaa").isEqualTo("xxx");
        //out.message(0).header("JMS_AMQP_MA_bbb").isEqualTo("yyy");

        send("hello", "Hello!", facade -> {
            try {
                facade.setApplicationProperty("aaa", "xxx");
                facade.setTracingAnnotation("bbb", "yyy");
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        out.assertIsSatisfied();
    }

    private void send(String queue, String body, Consumer<AmqpJmsMessageFacade> consumer) throws JMSException {
        ConnectionFactory connectionFactory = context.getComponent("amqp", AMQPComponent.class)
            .getConnectionFactory();
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession();
             MessageProducer producer = session.createProducer(session.createQueue(queue))) {
            TextMessage message = session.createTextMessage(body);
            consumer.accept((AmqpJmsMessageFacade) ((JmsMessage) message).getFacade());
            producer.send(message);
        }
    }

}
