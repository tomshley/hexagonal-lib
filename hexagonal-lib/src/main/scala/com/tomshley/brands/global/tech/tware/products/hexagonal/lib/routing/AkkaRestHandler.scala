package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.routing

import akka.http.scaladsl.server.Route

trait AkkaRestHandler {
  lazy val routes: Seq[Route] = throw new NotImplementedError(
    "This method must be implemented to use services. Ok if not using services"
  )
}
