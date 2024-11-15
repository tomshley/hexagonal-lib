package com.tomshley.hexagonal.lib.kafka.util

import com.google.protobuf.any.Any as ScalaPBAny
import org.apache.pekko.persistence.query.typed.EventEnvelope as PersistenceQueryEventEnvelope
import org.apache.pekko.projection.eventsourced.EventEnvelope as EventSourcedEventEnvelope
import org.apache.pekko.persistence.typed.PersistenceId
import scalapb.GeneratedMessage

final case class KafkaKeyMessageEnvelope(serviceName:String, key: String, pbValue: GeneratedMessage) {
  lazy val messageBytes: Array[Byte] = ScalaPBAny.pack(pbValue, serviceName).toByteArray
}

object KafkaKeyMessageEnvelope {
  def apply(serviceName:String, eventsourcedEnvelope: EventSourcedEventEnvelope[?], pbValue: GeneratedMessage) = new KafkaKeyMessageEnvelope(
    serviceName,
    PersistenceId.extractEntityId(eventsourcedEnvelope.persistenceId),
    pbValue
  )
  def apply(serviceName:String, persistenceQueryEnvelope: PersistenceQueryEventEnvelope[?], pbValue: GeneratedMessage) = new KafkaKeyMessageEnvelope(
    serviceName,
    PersistenceId.extractEntityId(persistenceQueryEnvelope.persistenceId),
    pbValue
  )
}