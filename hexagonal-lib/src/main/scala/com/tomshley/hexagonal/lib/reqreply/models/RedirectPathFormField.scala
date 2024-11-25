package com.tomshley.hexagonal.lib.reqreply.models

trait RedirectPathFormField {
  lazy val expiringFormFieldRedirectPathMaybe: Option[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(redirectPathFormFieldHmacString)
  }
  val redirectPathFormFieldHmacString: String
}
