package com.tomshley.hexagonal.lib.kafka.util

import io.confluent.kafka.schemaregistry.avro.AvroSchema
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException
import org.slf4j.{Logger, LoggerFactory}

import java.io.IOException
import scala.annotation.tailrec
import scala.collection.immutable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success, Try}

object SchemaPublication {

  final case class TopicSchemaSettings(topicSchemas: Map[String, AvroSchema],
                                       registryURL: String,
                                       identityMapCapacity: Int,
                                       retriesNum: Int,
                                       retriesInterval: Duration)
  object TopicSchemaSettings {
    def apply(
      topicSchema: Map[String, AvroSchema],
      registryURL: Option[String] = Option.empty,
      identityMapCapacity: Option[Int] = Option.empty,
      retriesNum: Option[Int] = Option.empty,
      retriesInterval: Option[Duration] = Option.empty
    ): TopicSchemaSettings = {
      new TopicSchemaSettings(
        topicSchema,
        registryURL.getOrElse("http://localhost:8081"),
        identityMapCapacity.getOrElse(200),
        retriesNum.getOrElse(5),
        retriesInterval.getOrElse(500.milliseconds)
      )
    }
  }
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def publishWithRetry(
    topicSchemaSettings: TopicSchemaSettings
  ): immutable.Iterable[Unit] = {
    val schemaRegistryClient = new CachedSchemaRegistryClient(
      topicSchemaSettings.registryURL,
      topicSchemaSettings.identityMapCapacity
    )

    topicSchemaSettings.topicSchemas.map((ts: (String, AvroSchema)) => {
      retryCallSchemaRegistry(logger)(
        topicSchemaSettings.retriesNum,
        topicSchemaSettings.retriesInterval, {
          schemaRegistryClient.register(ts._1, ts._2)
        }
      ) match {
        case failure @ Failure(_: IOException | _: RestClientException) =>
          failure.exception.printStackTrace()
        case _ =>
          logger.info(
            s"Schemas publication at: ${topicSchemaSettings.registryURL}"
          )
      }
    })

  }

  @tailrec
  private def retryCallSchemaRegistry(
    logger: Logger
  )(countdown: Int, interval: Duration, f: => Unit): Try[Unit] = {
    Try(f) match {
      case result @ Success(_) =>
        logger.info("Successfully call the Schema Registry.")
        result
      case result @ Failure(_) if countdown <= 0 =>
        logger.error("Fail to call the Schema Registry for the last time.")
        result
      case Failure(_) if countdown > 0 =>
        logger.error(
          s"Fail to call the Schema Registry, retry in ${interval.toSeconds} secs."
        )
        Thread.sleep(interval.toMillis)
        retryCallSchemaRegistry(logger)(countdown - 1, interval, f)
    }
  }
}
