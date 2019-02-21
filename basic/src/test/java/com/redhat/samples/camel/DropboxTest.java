package com.redhat.samples.camel;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dropbox.util.DropboxConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.util.ObjectHelper;
import org.junit.Test;

import static org.junit.Assume.assumeTrue;

public class DropboxTest extends CamelTestSupport {

    @PropertyInject("env:DROPBOX_ACCESS_TOKEN:")
    private String dropboxAccessToken;

    @PropertyInject("env:DROPBOX_CLIENT_IDENTIFIER:")
    private String dropboxClientIdentifier;

    @Override
    protected void doPreSetup() throws Exception {
        assumeTrue(ObjectHelper.isNotEmpty(dropboxAccessToken));
        assumeTrue(ObjectHelper.isNotEmpty(dropboxClientIdentifier));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                String filename = String.format("%s-%s.txt", DropboxTest.class.getSimpleName(),
                        new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

                // @formatter:off
                from("direct:in")
                    .setHeader(DropboxConstants.HEADER_PUT_FILE_NAME, constant(filename))
                    .toF("dropbox:put?accessToken=%s&clientIdentifier=%s&uploadMode=force&remotePath=/",
                        dropboxAccessToken, dropboxClientIdentifier)
                    .log("Uploaded file path = ${header.UPLOADED_FILE}")
                    .to("direct:download");

                from("direct:download")
                    .toF("dropbox:get?accessToken=%s&clientIdentifier=%s&remotePath=/%s",
                        dropboxAccessToken, dropboxClientIdentifier, filename)
                    .log("Downloaded file content = ${body}")
                    .to("mock:out");
                // @formatter:on
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
