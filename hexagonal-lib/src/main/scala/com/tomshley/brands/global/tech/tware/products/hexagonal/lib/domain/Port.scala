package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

trait Port[T1 <: Model, T2 <: Model] {
  val inboundModel:T1
  def liftPort: T2 = ???
}
