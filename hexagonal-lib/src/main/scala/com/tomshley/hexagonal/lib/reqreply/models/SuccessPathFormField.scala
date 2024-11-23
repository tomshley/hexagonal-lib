package com.tomshley.hexagonal.lib.reqreply.models

trait SuccessPathFormField {
  lazy val expiringSuccessPath: Option[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(successPath)
  }
  val successPath: String
}
