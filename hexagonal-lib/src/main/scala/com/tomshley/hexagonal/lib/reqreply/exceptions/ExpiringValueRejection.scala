package com.tomshley.hexagonal.lib.reqreply.exceptions

import org.apache.pekko.http.javadsl.{model, server}
import org.apache.pekko.http.scaladsl.server.RejectionWithOptionalCause

final case class ExpiringValueRejection(message: String, override val cause: Option[Throwable] = None)
  extends ReqReplyRejection
