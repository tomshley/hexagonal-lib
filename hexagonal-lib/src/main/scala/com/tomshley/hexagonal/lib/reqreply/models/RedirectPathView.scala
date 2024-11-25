package com.tomshley.hexagonal.lib.reqreply.models
import scala.concurrent.duration.*
trait RedirectPathView {
  lazy val expiringRedirectPath: ExpiringValue = {
    ExpiringValue(expirationDuration = Some((360 * 10).days), value=redirectPath)
  }
  lazy val redirectPathHmac: String = {
    expiringRedirectPath.toBase64Hmac
  }
  def redirectPath:Option[String]
}
