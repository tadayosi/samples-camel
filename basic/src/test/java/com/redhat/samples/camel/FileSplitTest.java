package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Before;
import org.junit.Test;

/**
 * https://issues.apache.org/jira/browse/CAMEL-12769
 */
public class FileSplitTest extends FileTestSupport {

    private static final String TEST_DIR = "/tmp/samples-camel/" + FileSplitTest.class.getSimpleName();

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                // @formatter:off
                onException(Throwable.class)
                    .maximumRedeliveries(0)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "ERROR: ${exception}");

                // input: *.csv
                fromF("file:%s/in?move=../../../${file:parent}_done/${file:name}&charset=Shift_JIS&include=.*\\.csv", TEST_DIR)
                    .split().tokenize(",").streaming()
                        .log("body = ${body}")
                        .to("mock:out");

                // input: *.xml
                fromF("file:%s/in?move=../../../${file:parent}_done/${file:name}&charset=Shift_JIS&include=.*\\.xml", TEST_DIR)
                    .split(xpath("/data/line/text()")).streaming()
                        .log("body = ${body}")
                        .to("mock:out");
                // @formatter:on
            }
        };
    }

    private static Path inputCsv = Paths.get(TEST_DIR, "in", "data.csv");
    private static Path inputXml = Paths.get(TEST_DIR, "in", "data.xml");

    @Before
    public void clearInputFiles() throws IOException {
        Files.deleteIfExists(inputCsv);
        Files.deleteIfExists(inputXml);
    }

    @Test
    public void csv() throws Exception {
        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(5);
        out.expectedBodiesReceived("あ", "い", "う", "え", "お");

        Files.write(inputCsv, "あ,い,う,え,お".getBytes("Shift_JIS"));

        out.assertIsSatisfied(10 * 1000);
    }

    @Test
    public void xml() throws Exception {
        MockEndpoint out = getMockEndpoint("mock:out");
        out.expectedMessageCount(5);
        out.expectedBodiesReceived("あ", "い", "う", "え", "お");

        Files.copy(
            getClass().getResourceAsStream("/" + getClass().getSimpleName() + "-input.xml"),
            inputXml);

        out.assertIsSatisfied(10 * 1000);
    }

}
