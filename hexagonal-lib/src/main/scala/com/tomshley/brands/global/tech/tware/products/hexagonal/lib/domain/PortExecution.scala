package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

import scala.concurrent.ExecutionContext

trait PortExecution[T1, T2] {
  def execute(inboundModel:T1): T2 = ???
}
