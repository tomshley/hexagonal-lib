package com.tomshley.hexagonal.lib.reqreply

import com.tomshley.hexagonal.lib.reqreply.exceptions.{ExpiringValueRejection, UnknownRejection}
import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue
import com.tomshley.hexagonal.lib.reqreply.models.ExpiringValue.ExpiringValueInvalid
import org.apache.pekko.http.scaladsl.server.{Directive, Directive1}
import org.apache.pekko.http.scaladsl.server.Directives.*

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait IdempotencyDirectives {

  def getRequestId(idempotency: Idempotency, expiringValue: ExpiringValue):Directive1[Idempotent.Summary] = {
      onComplete(idempotency.idempotencyResult(expiringValue)) flatMap {
        case Success(summary: Idempotent.Summary) =>
          summary.replyBody match
            case Some(_) => provide(summary)
            case None => reject(UnknownRejection("Unknown error occurred"))
        case Failure(_) =>
          reject(UnknownRejection("Unknown error occurred"))
      }
  }

  def idempotentRequestReply(idempotency: Idempotency, expiringValue: ExpiringValue, responseBodyCallback: => Future[Idempotency.RequestReply]): Directive1[Idempotency.RequestReply] = {
    onComplete(idempotency.reqReply(
      expiringValue,
      responseBodyCallback
    )) flatMap {
      case Success(requestReply: Idempotency.RequestReply) =>
        requestReply.body match
          case Some(_) => provide(requestReply)
          case None => reject(UnknownRejection("Unknown error occurred"))
      case scala.util.Failure(exception: ExpiringValueInvalid) =>
        reject(ExpiringValueRejection(exception.getMessage))
      case scala.util.Failure(exception: Exception) =>
        reject(UnknownRejection(exception.getMessage))
      case scala.util.Failure(_) =>
        reject(UnknownRejection("Unknown error occurred"))
    }
  }
}