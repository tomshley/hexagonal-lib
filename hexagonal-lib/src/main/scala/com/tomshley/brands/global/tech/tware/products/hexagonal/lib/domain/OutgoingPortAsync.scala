package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.domain

import scala.concurrent.Future

trait OutgoingPortAsync[T1 <: Model, T2 <: Future[OutgoingModel]] extends Port[T1, T2] with PortAsyncExecution[T1, T2]
