package com.tomshley.hexagonal.reqreply.models

trait IdempotentView {
  def requestId: IdempotentRequestId = IdempotentRequestId()
}
