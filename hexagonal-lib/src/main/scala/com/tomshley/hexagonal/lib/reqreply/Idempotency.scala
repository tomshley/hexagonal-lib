package com.tomshley.hexagonal.lib.reqreply

import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue
import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue.ExpiringValueInvalid
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.cluster.sharding.typed.scaladsl.ClusterSharding
import org.apache.pekko.util.Timeout
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

class Idempotency(system: ActorSystem[?]) {
  import system.executionContext

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private val idempotencyCluster = ClusterSharding(system)

  implicit private val timeout: Timeout =
    Timeout.create(
      system.settings.config
        .getDuration("tomshley-hexagonal-reqreply-idempotency.ask-timeout")
    )

  def reqReply(
                requestId: ExpiringValue,
                responseBodyCallback: => Future[Idempotency.RequestReply]
  ): Future[Idempotency.RequestReply] = {
    if (requestId.isExpired) {
      Future.failed(
        new ExpiringValueInvalid("You've made an expired request")
      )
    } else {
      val idempotentEntityRef =
        idempotencyCluster.entityRefFor(
          Idempotent.EntityKey,
          requestId.uuid.toString
        )

      val idempotentReply: Future[Idempotent.Summary] =
        idempotentEntityRef.askWithStatus(
          Idempotent.SingleRequest(Option.empty, Option.empty, _)
        )
      idempotentReply.flatMap(idempotentRequest => {
        if (!idempotentRequest.isIdempotent) {
          responseBodyCallback.map { response =>
            idempotentEntityRef
              .askWithStatus(
                Idempotent
                  .SingleReply(response.headers, response.body, _)
              )

            Idempotency.RequestReply(response.headers, response.body)
          }
        } else {
          logger.info(
            "idempotencyKey {} isIdempotent {};",
            idempotentRequest.idempotencyKey,
            idempotentRequest.isIdempotent
          )
          if (idempotentRequest.replyBody.isDefined) {
            Future.successful(
              Idempotency
                .RequestReply(Option.empty, idempotentRequest.replyBody)
            )
          } else {
            Future.failed(new Exception("You've made a duplicate request"))
          }
        }
      })
    }
  }
}

object Idempotency {
  def apply(system: ActorSystem[?]) = new Idempotency(system)

  final case class RequestReply(headers: Option[Map[String, String]],
                                body: Option[String])
}
