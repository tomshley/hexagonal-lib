package com.tomshley.hexagonal.lib.reqreply

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.apache.pekko.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityContext, EntityTypeKey}
import org.apache.pekko.pattern.StatusReply

import java.time.Instant
import scala.concurrent.ExecutionContextExecutor

object Idempotent {
  sealed trait Command

  sealed trait Event

  final case class SingleRequest(headers: Option[Map[String, String]],
                                 body: Option[String],
                                 replyTo: ActorRef[StatusReply[Summary]])
      extends Command

  final case class SingleReply(headers: Option[Map[String, String]],
                               body: Option[String],
                               replyTo: ActorRef[StatusReply[Summary]])
      extends Command

  final case class State(idempotencyKey: Option[String],
                         requestedOn: Option[Instant],
                         repliedOn: Option[Instant],
                         requestCount: Int,
                         replyCount: Int,
                         requestHeaders: Option[Map[String, String]],
                         requestBody: Option[String],
                         replyHeaders: Option[Map[String, String]],
                         replyBody: Option[String]) {
    def newRequest(idempotencyKey: String,
                   requestHeaders: Option[Map[String, String]],
                   requestBody: Option[String]): State = {
      copy(
        idempotencyKey = Some(idempotencyKey),
        requestedOn = Some(Instant.now()),
        repliedOn = repliedOn,
        requestCount = requestCount + 1,
        replyCount = replyCount,
        requestHeaders = requestHeaders,
        requestBody = requestBody,
        replyHeaders = replyHeaders,
        replyBody = replyBody
      )
    }
    def newReply(replyHeaders: Option[Map[String, String]],
                 replyBody: Option[String]): State = {
      copy(
        idempotencyKey = idempotencyKey,
        requestedOn = requestedOn,
        repliedOn = Some(Instant.now()),
        requestCount = requestCount,
        replyCount = replyCount + 1,
        requestHeaders = requestHeaders,
        requestBody = requestBody,
        replyHeaders = replyHeaders,
        replyBody = replyBody
      )
    }

    def existingRequest(): State = {
      copy(
        idempotencyKey = idempotencyKey,
        requestedOn = requestedOn,
        repliedOn = repliedOn,
        requestCount = requestCount + 1,
        replyCount = replyCount,
        requestHeaders = requestHeaders,
        requestBody = requestBody,
        replyHeaders = replyHeaders,
        replyBody = replyBody
      )
    }

    def existingReply(): State = {
      copy(
        idempotencyKey = idempotencyKey,
        requestedOn = requestedOn,
        repliedOn = repliedOn,
        requestCount = requestCount,
        replyCount = replyCount + 1,
        requestHeaders = requestHeaders,
        requestBody = requestBody,
        replyHeaders = replyHeaders,
        replyBody = replyBody
      )
    }

    def toSummary: Summary = {
      Summary(
        idempotencyKey.get,
        isIdempotent,
        repliedOn,
        replyHeaders,
        replyBody
      )
    }

    def isIdempotent: Boolean = {
      isIdempotentRequest || isIdempotentReply
    }

    def isIdempotentRequest: Boolean =
      requestCount > 1

    def isIdempotentReply: Boolean =
      replyCount > 1
  }

  final case class Summary(idempotencyKey: String,
                           isIdempotent: Boolean,
                           repliedOn: Option[Instant],
                           replyHeaders: Option[Map[String, String]],
                           replyBody: Option[String])

  object State {
    val empty: State =
      State(
        idempotencyKey = None,
        requestedOn = None,
        repliedOn = None,
        requestCount = 0,
        replyCount = 0,
        requestHeaders = None,
        requestBody = None,
        replyHeaders = None,
        replyBody = None
      )
  }

  val EntityKey: EntityTypeKey[Command] =
    EntityTypeKey[Command]("Idempotent")

  def init(system: ActorSystem[?]): Unit = {
    implicit val ec: ExecutionContextExecutor = system.executionContext

    val behaviorFactory: EntityContext[Command] => Behavior[Command] = {
      entityContext =>
        Idempotent(entityContext.entityId)
    }
    ClusterSharding(system).init(Entity(EntityKey)(behaviorFactory))
  }

  def apply(idempotentKey: String): Behavior[Command] =
    idempotency(idempotentKey, State.empty)

  private def idempotencyResult(
    idempotentKey: String,
    newState: State,
    replyTo: ActorRef[StatusReply[Summary]]
  ): Behavior[Command] = {
    val idempotencyResult = idempotency(idempotentKey, newState)
    replyTo ! StatusReply.Success(newState.toSummary)
    idempotencyResult
  }

  private def idempotency(idempotentKey: String,
                          state: State): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case SingleRequest(headers, body, replyTo) =>
          if (state.isIdempotentReply) {
            replyTo ! StatusReply.error(
              s"This idempotent request already has a reply"
            )
            Behaviors.same
          } else if (state.isIdempotentRequest && !state.isIdempotentReply) {
            idempotencyResult(idempotentKey, state.existingRequest(), replyTo)
          } else {
            idempotencyResult(
              idempotentKey,
              state.newRequest(idempotentKey, headers, body),
              replyTo
            )
          }
        case SingleReply(headers, body, replyTo) =>
          if (state.isIdempotentReply && !state.isIdempotentRequest) {
            replyTo ! StatusReply.error(s"Invalid idempotent state")
            Behaviors.same
          } else if (state.isIdempotentReply && state.isIdempotentRequest) {
            idempotencyResult(idempotentKey, state.existingReply(), replyTo)
          } else {
            idempotencyResult(
              idempotentKey,
              state.newReply(headers, body),
              replyTo
            )
          }
      }
    /*
      UNDER CONSTRUCTION
     */
    //      context.setReceiveTimeout(30.seconds, Idle)
    }
}
