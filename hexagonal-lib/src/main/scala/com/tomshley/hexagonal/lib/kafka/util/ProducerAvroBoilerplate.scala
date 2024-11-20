package com.tomshley.hexagonal.lib.kafka.util

import io.confluent.kafka.serializers.*
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.*
import org.apache.pekko.actor.CoordinatedShutdown
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.ProducerSettings
import org.apache.pekko.kafka.scaladsl.SendProducer
import scala.jdk.CollectionConverters.*

object ProducerAvroBoilerplate extends CreateProducer[String, SpecificRecord]{
  override def producerSettings(system: ActorSystem[_]): ProducerSettings[String, SpecificRecord] = {
    val kafkaAvroSerDeConfig = Map[String, Any](
      AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG -> "http://localhost:23434",
      KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG -> true.toString)

    val kafkaAvroSerializer = new KafkaAvroSerializer()
    kafkaAvroSerializer.configure(kafkaAvroSerDeConfig.asJava, false)
    val serializer = kafkaAvroSerializer.asInstanceOf[Serializer[SpecificRecord]]

    ProducerSettings(system, new StringSerializer, serializer)
  }
}
