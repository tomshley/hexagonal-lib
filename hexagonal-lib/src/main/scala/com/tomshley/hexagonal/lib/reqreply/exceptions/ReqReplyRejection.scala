package com.tomshley.hexagonal.lib.reqreply.exceptions

import org.apache.pekko.http.javadsl.{model, server}
import org.apache.pekko.http.scaladsl.server.RejectionWithOptionalCause

trait ReqReplyRejection extends server.Rejection with RejectionWithOptionalCause {
  val message: String
  val cause: Option[Throwable] = None
}
