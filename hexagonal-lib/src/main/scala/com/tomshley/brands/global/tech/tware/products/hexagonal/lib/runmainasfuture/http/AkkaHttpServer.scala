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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.http

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.{complete, concat, get, path}
import akka.http.scaladsl.server.Route
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.core.RunMainFutureSugar
import org.joda.time.DateTime

import scala.concurrent.Future

trait AkkaHttpServer extends RunMainFutureSugar[ServerBinding, Route] {
  private var serviceDefinitions:List[Route] = List(
    get {
      path("heartbeat") {
        complete(new DateTime().toString)
      }
    }
  )

  lazy val serverBuilder = Http().newServerAt(serverProperties.hostname, serverProperties.port)

  override lazy val serverCreation: Future[ServerBinding] = {
    serverBuilder.bind(concat(serviceDefinitions *))
  }
  override lazy val serverTermination: Future[Done] = {
    serviceDefinitions = null
    serverCreation.flatMap(_.unbind())
  }
  given system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, serverProperties.actorSystemName)

  override def addServices(services: Route*): Unit = {
    serviceDefinitions = serviceDefinitions :++ services
  }
}
