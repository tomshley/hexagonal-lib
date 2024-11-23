package com.tomshley.hexagonal.lib.reqreply.models

trait ExpiringSuccessView {
  def successPath: ExpiringValue = ExpiringValue(value = Some(successPathTargetRoute))
  val successPathTargetRoute:String
}
