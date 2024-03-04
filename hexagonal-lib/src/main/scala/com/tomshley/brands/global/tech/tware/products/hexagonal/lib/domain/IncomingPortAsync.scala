package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

import scala.concurrent.{ExecutionContext, Future}

trait IncomingPortAsync[T1 <: PortInboundModel, T2 <: Future[Model]] extends Port[T1, T2] with PortAsyncExecution[T1, T2]
