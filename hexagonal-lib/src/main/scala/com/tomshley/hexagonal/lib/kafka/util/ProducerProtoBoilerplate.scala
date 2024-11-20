package com.tomshley.hexagonal.lib.kafka.util

import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.ProducerSettings

object ProducerProtoBoilerplate extends CreateProducer[String, Array[Byte]] {
  override def producerSettings(system: ActorSystem[?]): ProducerSettings[String, Array[Byte]] = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
}