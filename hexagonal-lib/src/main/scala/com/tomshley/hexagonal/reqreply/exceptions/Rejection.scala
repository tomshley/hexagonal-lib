package com.tomshley.hexagonal.reqreply.exceptions

import org.apache.pekko.http.javadsl.{model, server}
import org.apache.pekko.http.scaladsl.server.RejectionWithOptionalCause

trait ReqReplyRejection extends server.Rejection with RejectionWithOptionalCause {
  val message: String
  val cause: Option[Throwable] = None
}
final case class IdempotentRejection(message: String, override val cause: Option[Throwable] = None)
  extends ReqReplyRejection

final case class UnknownRejection(message: String, override val cause: Option[Throwable] = None)
  extends ReqReplyRejection

