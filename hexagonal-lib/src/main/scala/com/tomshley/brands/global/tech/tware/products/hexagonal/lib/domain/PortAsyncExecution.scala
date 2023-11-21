package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

import scala.concurrent.ExecutionContext

trait PortAsyncExecution[T1, T2] {
  def executeAsync(inboundModel:T1)(implicit ec: ExecutionContext): T2 = ???
}
