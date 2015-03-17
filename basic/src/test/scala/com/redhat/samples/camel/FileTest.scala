package com.redhat.samples.camel

import java.io.File
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.component.mock.MockEndpoint
import org.hamcrest.Matchers._
import org.junit.Assert._
import org.junit.Test
import com.google.common.io.Files
import scala.io.Source
import org.junit.Before

class FileTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure {
      from("file:target/in?move=../../${file:parent}_done/${file:name}")
        .transform().simple("<<< ${body} >>>")
        .to("file:target/out")
    }
  }

  @Before
  def deleteOutFile {
    new File("target/out/hello.txt").delete
  }

  @Test
  def hello {
    Files.write("Hello!".getBytes, new File("target/in/hello.txt"))

    Thread.sleep(1000)

    val outFile = new File("target/out/hello.txt")
    assertThat(outFile.exists, is(true))
    assertThat(Source.fromFile(outFile).getLines.mkString, is("<<< Hello! >>>"))
  }

}