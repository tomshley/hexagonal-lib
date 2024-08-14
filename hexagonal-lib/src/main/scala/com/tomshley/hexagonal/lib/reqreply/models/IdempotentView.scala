package com.tomshley.hexagonal.lib.reqreply.models

trait IdempotentView {
  def requestId: IdempotentRequestId = IdempotentRequestId()
}
