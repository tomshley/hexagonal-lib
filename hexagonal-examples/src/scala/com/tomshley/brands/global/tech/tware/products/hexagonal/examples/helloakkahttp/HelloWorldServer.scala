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

package com.tomshley.brands.global.tech.tware.products.hexagonal.examples.helloakkahttp

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{ complete, concat, get, path, pathSingleSlash }
import akka.http.scaladsl.server.Route
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.{ AkkaHttpServer, ServerProperties }
object HelloWorldServer extends AkkaHttpServer {
  override lazy val serverProperties = ServerProperties(hostname = "localhost", port = 80081)
  private val myEndpoint: Route =
    get {
      concat(path("helloworld") {
        complete("hello")
      })
    }

  addService(myEndpoint)
  run
}
