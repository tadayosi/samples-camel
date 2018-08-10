package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.redhat.samples.camel.helpers.SSHServer;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SftpToFileTest extends FileTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                onException(Throwable.class)
                    .maximumRedeliveries(0)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "ERROR: ${exception}");

                from("sftp://sample@localhost:2222/in?password=password&delete=true&charset=ISO-8859-1")
                    .to("file:/tmp/samples-camel/SftpToFileTest/out?charset=UTF-8");
            }
        };
    }

    private static Path inFile = Paths.get("target/sftp/in/hello.txt");
    private static Path outFile = Paths.get("/tmp/samples-camel/SftpToFileTest/out/hello.txt");

    private static SSHServer sshServer;

    @BeforeClass
    public static void deleteOutFile() throws IOException {
        Files.deleteIfExists(outFile);
    }

    @BeforeClass
    public static void startServer() throws Exception {
        sshServer = new SSHServer(2222).start();
    }

    @AfterClass
    public static void stopServer() throws IOException {
        sshServer.stop();
        sshServer = null;
    }

    @Test
    public void hello() throws Exception {
        Files.write(inFile, "ä".getBytes("ISO-8859-1"));

        Thread.sleep(3000);

        assertOutFile(outFile, "ä", "UTF-8");
    }

}
