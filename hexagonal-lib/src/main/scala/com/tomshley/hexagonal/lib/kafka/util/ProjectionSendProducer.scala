package com.tomshley.hexagonal.lib.kafka.util

import org.apache.kafka.common.serialization.{
  ByteArraySerializer,
  StringSerializer
}
import org.apache.pekko.actor.CoordinatedShutdown
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.kafka.ProducerSettings
import org.apache.pekko.kafka.scaladsl.SendProducer

trait ProjectionSendProducer {
  protected def createProducer(
    system: ActorSystem[?]
  ): SendProducer[String, Array[Byte]] = {
    val producerSettings =
      ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
    val sendProducer =
      SendProducer(producerSettings)(system)
    CoordinatedShutdown(system).addTask(
      CoordinatedShutdown.PhaseBeforeActorSystemTerminate,
      "close-sendProducer"
    ) { () =>
      sendProducer.close()
    }
    sendProducer
  }
}
