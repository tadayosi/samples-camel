package com.redhat.samples.camel.ftp

import java.io.File
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.component.mock.MockEndpoint
import org.hamcrest.Matchers._
import org.junit.Assert._
import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import org.junit.Before
import org.junit.After
import scala.io.Source

class FtpTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure {
      from("direct:in")
        .transform().simple("<<< ${body} >>>")
        .setHeader(Exchange.FILE_NAME, constant("out.txt"))
        .to("ftp:sample:password@localhost:2121/out")
    }
  }

  private var ftpServer: Option[FTPServer] = None

  @Before
  def startFtpServer {
    ftpServer = Some(new FTPServer(2121).start)
  }

  @Before
  def deleteFile {
    new File("build/ftp/out/out.txt").delete
  }

  @After
  def stopFtpServer {
    ftpServer.foreach(_.stop)
  }

  @Test
  def hello {
    template.sendBody("direct:in", "Hello!")
    Thread.sleep(1000)

    val outFile = new File("build/ftp/out/out.txt")
    assertThat(outFile.exists, is(true))
    assertThat(Source.fromFile(outFile).getLines.mkString, is("<<< Hello! >>>"))
  }

}
