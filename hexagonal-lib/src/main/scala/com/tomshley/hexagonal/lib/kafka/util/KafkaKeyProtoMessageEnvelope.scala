package com.tomshley.hexagonal.lib.kafka.util

import com.google.protobuf.any.Any as ScalaPBAny
import org.apache.pekko.persistence.query.typed.EventEnvelope as PersistenceQueryEventEnvelope
import org.apache.pekko.projection.eventsourced.EventEnvelope as EventSourcedEventEnvelope
import org.apache.pekko.persistence.typed.PersistenceId
import scalapb.GeneratedMessage

@deprecated
val KafkaKeyMessageEnvelope = KafkaKeyProtoMessageEnvelope

final case class KafkaKeyProtoMessageEnvelope(serviceName:String, key: String, pbValue: GeneratedMessage) {
  lazy val messageBytes: Array[Byte] = ScalaPBAny.pack(pbValue, serviceName).toByteArray
}

object KafkaKeyProtoMessageEnvelope {
  def apply(serviceName:String, eventsourcedEnvelope: EventSourcedEventEnvelope[?], pbValue: GeneratedMessage) = new KafkaKeyProtoMessageEnvelope(
    serviceName,
    PersistenceId.extractEntityId(eventsourcedEnvelope.persistenceId),
    pbValue
  )
  def apply(serviceName:String, persistenceQueryEnvelope: PersistenceQueryEventEnvelope[?], pbValue: GeneratedMessage) = new KafkaKeyProtoMessageEnvelope(
    serviceName,
    PersistenceId.extractEntityId(persistenceQueryEnvelope.persistenceId),
    pbValue
  )
}