package com.redhat.samples.camel

import java.io.File

import com.redhat.samples.camel.helpers.FTPServer
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.assertj.core.api.Assertions._
import org.junit.{After, Before, Test}

import scala.io.Source

class FtpTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      from("direct:in")
        .transform().simple("<<< ${body} >>>")
        .setHeader(Exchange.FILE_NAME, constant("out.txt"))
        .to("ftp:sample:password@localhost:2121/out")
    }
  }

  private var ftpServer: Option[FTPServer] = None

  @Before
  def startFtpServer: Unit = {
    ftpServer = Some(new FTPServer(2121).start)
  }

  @Before
  def deleteFile: Unit = {
    new File("target/ftp/out/out.txt").delete
  }

  @After
  def stopFtpServer: Unit = {
    ftpServer.foreach(_.stop)
  }

  @Test
  def hello: Unit = {
    template.sendBody("direct:in", "Hello!")
    Thread.sleep(1000)

    val outFile = new File("target/ftp/out/out.txt")
    assertThat(outFile.exists).isEqualTo(true)
    assertThat(Source.fromFile(outFile).getLines.mkString).isEqualTo("<<< Hello! >>>")
  }

}
