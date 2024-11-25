package com.tomshley.hexagonal.lib.reqreply.models

trait RenderPathFormField {
  lazy val expiringRenderPathMaybe: Option[ExpiringValue] = {
    ExpiringValue.fromBase64Hmac(renderPathHmacString)
  }
  val renderPathHmacString: String
}
