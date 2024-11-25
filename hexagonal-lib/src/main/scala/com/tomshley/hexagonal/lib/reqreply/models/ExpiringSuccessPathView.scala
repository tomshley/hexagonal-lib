package com.tomshley.hexagonal.lib.reqreply.models

import scala.concurrent.duration.*
trait ExpiringSuccessPathView {
  lazy val successPathExpiringValue: ExpiringValue = ExpiringValue(expirationDuration = Some(1.day), value = successValue)
  def successValue: Option[String]
  lazy val successPathHmac: String = successPathExpiringValue.toBase64Hmac
}
