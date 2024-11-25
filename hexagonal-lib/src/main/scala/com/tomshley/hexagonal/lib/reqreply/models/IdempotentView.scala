package com.tomshley.hexagonal.lib.reqreply.models

trait IdempotentView {
  lazy val requestId: ExpiringValue = ExpiringValue()
  lazy val requestIdHmac: String = requestId.toBase64Hmac
}
