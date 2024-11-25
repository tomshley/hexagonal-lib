package com.tomshley.hexagonal.lib.reqreply

import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, EntityRef}
import org.apache.pekko.util.Timeout
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

class Idempotency(system: ActorSystem[?]) {

  import system.executionContext

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  final lazy val idempotencyCluster = ClusterSharding(system)

  implicit private val timeout: Timeout =
    Timeout.create(
      system.settings.config
        .getDuration("tomshley-hexagonal-reqreply-idempotency.ask-timeout")
    )

  private def idempotencyEntityRef(requestId: ExpiringValue): EntityRef[Idempotent.Command] = {
    idempotencyCluster.entityRefFor(
      Idempotent.EntityKey,
      requestId.uuid.toString
    )
  }

  def idempotencyResult(requestId: ExpiringValue, resultEntityRef: Option[EntityRef[Idempotent.Command]] = None): Future[Idempotent.Summary] = {
    resultEntityRef.getOrElse(idempotencyEntityRef(requestId)).askWithStatus(
      Idempotent.SingleRequest(Option.empty, Option.empty, _)
    )
  }

  def reqReply(
                requestId: ExpiringValue,
                responseBodyCallback: => Future[Idempotency.RequestReply]
              ): Future[Idempotency.RequestReply] = {
    val entityRef = idempotencyEntityRef(requestId)
    idempotencyResult(requestId, Some(entityRef))
      .flatMap(idempotentRequest => {
        if (!idempotentRequest.isIdempotent) {
          responseBodyCallback.map { (response: Idempotency.RequestReply) =>
            entityRef.askWithStatus(
              Idempotent
                .SingleReply(response.headers, response.body, _)
            ) match
              // Silently fail pass
              case _ => Idempotency.RequestReply(response.headers, response.body)
          }
        } else {
          logger.info(
            "idempotencyKey {} isIdempotent {};",
            idempotentRequest.idempotencyKey,
            idempotentRequest.isIdempotent
          )
          Future.successful(
            Idempotency.RequestReply(Option.empty, idempotentRequest.replyBody)
          )
        }
      })
  }
}

object Idempotency {
  def apply(system: ActorSystem[?]) = new Idempotency(system)

  final case class RequestReply(headers: Option[Map[String, String]],
                                body: Option[String])
}
