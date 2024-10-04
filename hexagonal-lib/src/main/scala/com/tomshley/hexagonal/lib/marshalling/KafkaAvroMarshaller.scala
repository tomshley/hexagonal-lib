package com.tomshley.hexagonal.lib.marshalling

import com.sksamuel.avro4s.RecordFormat
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.common.serialization.Serializer

trait KafkaAvroMarshaller {
  def forType[T](implicit format: RecordFormat[T]): Serializer[T] =
    new Serializer[T] {
      val ser = new KafkaAvroSerializer()

      override def configure(
                              configs: java.util.Map[String, ?],
                              isKey: Boolean
                            ): Unit =
        ser.configure(configs, isKey)

      override def serialize(topic: String, data: T): Array[Byte] = Option(data)
        .map(data => ser.serialize(topic, format.to(data)))
        .getOrElse(Array.emptyByteArray)

      override def close(): Unit = ser.close()
    }
}
