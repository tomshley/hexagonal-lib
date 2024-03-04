package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

trait IncomingPort[T1 <: PortInboundModel, T2 <: Model] extends Port[T1, T2] with PortExecution[T1, T2]
