package com.tomshley.hexagonal.lib.reqreply.models

trait IdempotentFormField {
  lazy val idempotentRequestId: Option[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(requestId)
  }
  val requestId: String
}
