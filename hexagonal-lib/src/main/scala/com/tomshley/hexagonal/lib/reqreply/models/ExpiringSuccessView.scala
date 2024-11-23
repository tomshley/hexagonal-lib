package com.tomshley.hexagonal.lib.reqreply.models

trait ExpiringSuccessView {
  def successPath: ExpiringValue = ExpiringValue()
}
