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

package com.tomshley.brands.global.tech.tware.products.hexagonal.examples.hellogrpc

import com.tomshley.brands.global.tech.tware.products.hexagonal.examples.hellogrpc.helloworld.{Greeting, HelloWorldGrpc, ToBeGreeted}
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.{GrpcServer, ServerProperties}
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.simplelogger.SLogger

import scala.concurrent.Future

object HelloWorldServer extends GrpcServer with SLogger {
  logger.error("foo")

  override lazy val serverProperties = ServerProperties(hostname = "localhost", port = 50051)

  private class HelloWorldService extends HelloWorldGrpc.HelloWorld {
    def sayHello(request: ToBeGreeted): Future[Greeting] = {
      val greetedPerson = request.person match {
        case Some(person) => person.name
        case None => "anonymous"
      }
      Future.successful(Greeting(message = s"Hello ${greetedPerson}!"))
    }
  }

  addService(HelloWorldGrpc.bindService(new HelloWorldService(), ec))

  run
}
