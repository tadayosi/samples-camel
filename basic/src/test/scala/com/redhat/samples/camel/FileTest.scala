package com.redhat.samples.camel

import java.io.File

import com.google.common.io.Files
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.assertj.core.api.Assertions._
import org.junit.{Before, Test}

import scala.io.Source

class FileTest extends CamelTestSupport {

  override def createRouteBuilder = new RouteBuilder {
    override def configure: Unit = {
      from("file:/tmp/sample/in?move=../../../${file:parent}_done/${file:name}")
        .transform().simple("<<< ${body} >>>")
        .to("file:/tmp/sample/out")
    }
  }

  @Before
  def deleteOutFile: Unit = {
    new File("/tmp/sample/out/hello.txt").delete
  }

  @Test
  def hello: Unit = {
    Files.write("Hello!".getBytes, new File("/tmp/sample/in/hello.txt"))

    Thread.sleep(3000)

    val outFile = new File("/tmp/sample/out/hello.txt")
    assertThat(outFile.exists).isEqualTo(true)
    assertThat(Source.fromFile(outFile).getLines.mkString).isEqualTo("<<< Hello! >>>")
  }

}
