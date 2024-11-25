package com.tomshley.hexagonal.lib.reqreply.models

trait ExpiringSuccessPathFormField {
  lazy val expiringSuccessPathMaybe: Option[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(successPathHmacString)
  }
  val successPathHmacString: String
}
