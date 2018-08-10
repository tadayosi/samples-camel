package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.redhat.samples.camel.helpers.FTPServer;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FtpToFileTest extends FileTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Throwable.class)
                    .maximumRedeliveries(0)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "ERROR: ${exception}");

                from("ftp://sample:password@localhost:2121/in?delete=true&charset=Shift_JIS")
                    .to("file:/tmp/samples-camel/FtpToFileTest/out?charset=EUC-JP");
            }
        };
    }

    private static Path inFile = Paths.get("target/ftp/in/hello.txt");
    private static Path outFile = Paths.get("/tmp/samples-camel/FtpToFileTest/out/hello.txt");

    private static FTPServer ftpServer;

    @BeforeClass
    public static void deleteOutFile() throws IOException {
        Files.deleteIfExists(outFile);
    }

    @BeforeClass
    public static void startServer() throws Exception {
        ftpServer = new FTPServer(2121).start();
    }

    @AfterClass
    public static void stopServer() {
        ftpServer.stop();
        ftpServer = null;
    }

    @Test
    public void hello() throws Exception {
        Files.write(inFile, "こんにちは".getBytes("Shift_JIS"));

        Thread.sleep(3000);

        assertOutFile(outFile, "こんにちは", "EUC-JP");
    }

}
