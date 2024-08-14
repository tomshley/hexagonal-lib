package com.tomshley.hexagonal.lib.cloudevents

import com.sksamuel.avro4s.RecordFormat
import com.tomshley.hexagonal.lib.marshalling.KafkaAvroMarshaller
import org.apache.kafka.common.serialization.Serializer

import java.util.Properties
import scala.jdk.CollectionConverters.*

/*
------------------ Message -------------------

Topic Name: mytopic

------------------- key ----------------------

Key: mykey

------------------ headers -------------------

ce_specversion: "1.0"
ce_type: "com.example.someevent"
ce_source: "/mycontext/subcontext"
ce_id: "1234-1234-1234"
//ce_time: "2018-04-05T03:56:24Z"
content-type: application/avro
       .... further attributes ...

------------------- value --------------------

            ... application data encoded in Avro ...

-----------------------------------------------
 */

object CloudEventKafkaAvroSerializer extends KafkaAvroMarshaller {
//  def init(registryURL: String): Serializer[CloudEvent] = {
//
//    val serdeProps = new Properties()
//    serdeProps.put("schema.registry.url", registryURL)
//
//    implicit lazy val format: RecordFormat[CloudEvent] =
//      RecordFormat[CloudEvent]
//    val valueSerializer: Serializer[CloudEvent] = forType[CloudEvent]
//    valueSerializer.configure(serdeProps.asScala.toMap.asJava, false)
//
//    valueSerializer
//  }
}
