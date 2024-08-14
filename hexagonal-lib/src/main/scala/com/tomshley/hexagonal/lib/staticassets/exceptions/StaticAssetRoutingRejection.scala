package com.tomshley.hexagonal.lib.staticassets.exceptions

import org.apache.pekko.http.javadsl.server
import org.apache.pekko.http.scaladsl.server.RejectionWithOptionalCause


final case class StaticAssetRoutingRejection(
                                              message: String,
                                              override val cause: Option[Throwable] = None
                                            ) extends server.Rejection
  with RejectionWithOptionalCause
