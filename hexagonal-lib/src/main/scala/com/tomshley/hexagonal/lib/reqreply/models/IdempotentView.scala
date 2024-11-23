package com.tomshley.hexagonal.lib.reqreply.models

trait IdempotentView {
  def requestId: ExpiringValue = ExpiringValue()
}
