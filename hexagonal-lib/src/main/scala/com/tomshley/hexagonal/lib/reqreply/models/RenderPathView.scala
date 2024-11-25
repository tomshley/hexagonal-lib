package com.tomshley.hexagonal.lib.reqreply.models
import scala.concurrent.duration.*
trait RenderPathView {
  lazy val expiringValueRenderPath: ExpiringValue = {
    ExpiringValue(expirationDuration = Some((360 * 10).days), value=renderPath)
  }
  lazy val renderPathHmac: String = {
    expiringValueRenderPath.toBase64Hmac
  }
  def renderPath:Option[String]
}
