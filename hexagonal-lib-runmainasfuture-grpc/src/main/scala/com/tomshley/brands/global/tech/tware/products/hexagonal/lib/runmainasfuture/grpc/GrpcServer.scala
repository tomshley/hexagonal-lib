/*
 * Copyright 2023 Tomshley LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * @author Thomas Schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 */

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.grpc

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.common.RunMainFutureSugar

import scala.concurrent.Future

// nb: this import is mandated by the addServices method
import akka.Done
import io.grpc.{Server, ServerBuilder, ServerServiceDefinition}

trait GrpcServer extends RunMainFutureSugar[Server, ServerServiceDefinition] {
  override lazy val serverTermination: Future[Done] = {
    logger.debug("shotdown hook call")

    logger.debug("awaiting termination call")
    serverCreation.map(_.shutdownNow())

    Future(akka.Done)
  }
  override lazy val serverCreation = {
    logger.debug("Server Creation Called")

    val server: Server = serverBuilder.build.start

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = server.shutdown()
    })

    logger.debug("Server Creation Created. Returning Future")

    Future(server)
  }
  private[this] lazy val serverBuilder = ServerBuilder.forPort(serverProperties.port)

  override def addServices(services: ServerServiceDefinition*): Unit = {
    logger.debug("Add Service Called")
    services.foreach(s => serverBuilder.addService(s))
    logger.debug("Service added to server builder")
  }
}
