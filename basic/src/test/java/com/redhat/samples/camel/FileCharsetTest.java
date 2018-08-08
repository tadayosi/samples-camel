package com.redhat.samples.camel;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.redhat.samples.camel.helpers.FTPServer;
import com.redhat.samples.camel.helpers.SSHServer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class FileCharsetTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("file:/tmp/sample/in?move=../../../${file:parent}_done/${file:name}&charset=Shift_JIS")
                    .log("body = ${body}")
                    .transform().simple("<<< ${body} >>>")
                    .log("body = ${body}")
                    .to("file:/tmp/sample/out?charset=EUC-JP");

                from("ftp://sample:password@localhost:2121/in/?delete=true&charset=Shift_JIS")
                    .log("body = ${body}")
                    .transform().simple("<<< ${body} >>>")
                    .log("body = ${body}")
                    .to("file:/tmp/sample/out?charset=EUC-JP");

                from("sftp://sample@localhost:2222/in?password=password&delete=true&charset=Shift_JIS")
                    .log("body = ${body}")
                    .transform().simple("<<< ${body} >>>")
                    .log("body = ${body}")
                    .to("file:/tmp/sample/out?charset=EUC-JP");
            }
        };
    }

    private Path inFile = Paths.get("/tmp/sample/in/hello.txt");
    private Path outFile = Paths.get("/tmp/sample/out/hello.txt");

    private Path inFileFtp = Paths.get("target/ftp/in/hello-ftp.txt");
    private Path outFileFtp = Paths.get("/tmp/sample/out/hello-ftp.txt");

    private Path inFileSftp = Paths.get("target/sftp/in/hello-sftp.txt");
    private Path outFileSftp = Paths.get("/tmp/sample/out/hello-sftp.txt");

    private static FTPServer ftpServer;
    private static SSHServer sshServer;

    @Before
    public void deleteOutFile() throws IOException {
        Files.deleteIfExists(outFile);
        Files.deleteIfExists(outFileFtp);
        Files.deleteIfExists(outFileSftp);
    }

    @BeforeClass
    public static void startServers() throws Exception {
        ftpServer = new FTPServer(2121).start();
        sshServer = new SSHServer(2222).start();
    }

    @AfterClass
    public static void stopServers() throws Exception {
        ftpServer.stop();
        ftpServer = null;
        sshServer.stop();
        sshServer = null;
    }

    @Test
    public void helloFile() throws Exception {
        Files.write(inFile, "こんにちは".getBytes("Shift_JIS"));

        Thread.sleep(3000);

        assertThat(Files.exists(outFile), is(true));
        assertThat(
            String.join(
                "\n",
                Files.readAllLines(outFile, Charset.forName("EUC-JP"))),
            is("<<< こんにちは >>>"));
    }

    @Test
    public void helloFtp() throws Exception {
        Files.write(inFileFtp, "こんにちは".getBytes("Shift_JIS"));

        Thread.sleep(3000);

        assertThat(Files.exists(outFileFtp), is(true));
        assertThat(
            String.join(
                "\n",
                Files.readAllLines(outFileFtp, Charset.forName("EUC-JP"))),
            is("<<< こんにちは >>>"));
    }

    @Test
    public void helloSftp() throws Exception {
        Files.write(inFileSftp, "こんにちは".getBytes("Shift_JIS"));

        Thread.sleep(3000);

        assertThat(Files.exists(outFileSftp), is(true));
        assertThat(
            String.join(
                "\n",
                Files.readAllLines(outFileSftp, Charset.forName("EUC-JP"))),
            is("<<< こんにちは >>>"));
    }

}
