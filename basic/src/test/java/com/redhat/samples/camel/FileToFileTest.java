package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileToFileTest extends FileTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Throwable.class)
                    .maximumRedeliveries(0)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "ERROR: ${exception}");

                from("file:/tmp/samples-camel/FileToFileTest/in?move=../../../${file:parent}_done/${file:name}&charset=Shift_JIS")
                    .to("file:/tmp/samples-camel/FileToFileTest/out?charset=EUC-JP");
            }
        };
    }

    private static Path inFile = Paths.get("/tmp/samples-camel/FileToFileTest/in/hello.txt");
    private static Path outFile = Paths.get("/tmp/samples-camel/FileToFileTest/out/hello.txt");

    @BeforeClass
    public static void deleteOutFile() throws IOException {
        Files.deleteIfExists(outFile);
    }

    @Test
    public void hello() throws Exception {
        Files.write(inFile, "こんにちは".getBytes("Shift_JIS"));

        Thread.sleep(3000);

        assertOutFile(outFile, "こんにちは", "EUC-JP");
    }

}
