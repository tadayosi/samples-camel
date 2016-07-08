package com.redhat.samples.camel.spring

import org.apache.camel.{Exchange, Processor, ProducerTemplate}
import org.slf4j.LoggerFactory

class SampleProducer extends Processor {

  private val logger = LoggerFactory.getLogger(classOf[SampleProducer])

  private var producer: Option[ProducerTemplate] = None

  def setProducer(producer: ProducerTemplate): Unit = {
    this.producer = Some(producer)
  }

  override def process(in: Exchange): Unit = {
    logger.info("body = {}", in.getIn.getBody)
    producer.foreach(_.send(new Processor {
      override def process(out: Exchange): Unit = {
        out.getIn.setBody(in.getIn.getBody)
      }
    }))
  }

}
