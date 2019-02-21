package com.redhat.samples.camel;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.box.sdk.BoxFile;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.box.BoxComponent;
import org.apache.camel.component.box.BoxConfiguration;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ObjectHelper;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

public class BoxTest extends CamelTestSupport {

    @PropertyInject("env:BOX_USERNAME:")
    private String boxUsername;

    @PropertyInject("env:BOX_PASSWORD:")
    private String boxPassword;

    @PropertyInject("env:BOX_CLIENT_ID:")
    private String boxClientId;

    @PropertyInject("env:BOX_CLIENT_SECRET:")
    private String boxClientSecret;

    @Override
    protected void doPreSetup() throws Exception {
        assumeTrue(ObjectHelper.isNotEmpty(boxUsername));
        assumeTrue(ObjectHelper.isNotEmpty(boxPassword));
        assumeTrue(ObjectHelper.isNotEmpty(boxClientId));
        assumeTrue(ObjectHelper.isNotEmpty(boxClientSecret));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                setup(getContext().getComponent("box", BoxComponent.class));

                String filename = String.format("%s-%s.txt", BoxTest.class.getSimpleName(),
                        new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

                // @formatter:off
                from("direct:in")
                    .toF("box:files/upload?inBody=content&parentFolderId=0&fileName=%s",
                        filename)
                    .setBody(exchange -> exchange.getIn().getBody(BoxFile.class).getID())
                    .log("Uploaded file id = ${body}")
                    .to("direct:download");

                from("direct:download")
                    .setHeader("CamelBox.fileId", body())
                    .setHeader("CamelBox.output", () -> new ByteArrayOutputStream())
                    .to("box:files/download")
                    .log("Downloaded file content = ${body}")
                    .to("mock:out");
                // @formatter:on
            }
        };
    }

    private void setup(BoxComponent box) {
        BoxConfiguration config = new BoxConfiguration();
        config.setAuthenticationType(BoxConfiguration.STANDARD_AUTHENTICATION);
        config.setUserName(boxUsername);
        config.setUserPassword(boxPassword);
        config.setClientId(boxClientId);
        config.setClientSecret(boxClientSecret);
        box.setConfiguration(config);
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
