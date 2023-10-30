package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

trait Port[T1 <: Model, T2 <: Model] {
  def execute(inboundModel:T1): T2 = ???
}
