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

import io.grpc.ManagedChannel
import com.tomshley.brands.global.tech.tware.products.hexagonal.examples.hellogrpc.helloworld.HelloWorldGrpc.{ HelloWorldBlockingStub, HelloWorldStub }
import com.tomshley.brands.global.tech.tware.products.hexagonal.examples.hellogrpc.helloworld.ToBeGreeted.Person
import com.tomshley.brands.global.tech.tware.products.hexagonal.examples.hellogrpc.helloworld.{ Greeting, HelloWorldGrpc, ToBeGreeted }
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.{ GrpcClient, ServerProperties }

import scala.concurrent.Future

object HelloWorldClient extends GrpcClient {
  override lazy val serverProperties = ServerProperties(hostname = "localhost", port = 50051)

  override lazy val serverTermination = {
    serverCreation.map(_.shutdownNow())
    Future(akka.Done)
  }

  override def channelStubs(channel: ManagedChannel): Unit = {
    val person = Person(name = "Bob")
    val toBeGreeted = ToBeGreeted(Some(person))
    // or use the generated builder methods
    val toBeGreeted2 = ToBeGreeted().withPerson(Person(name = "Bob"))
    // async client
    val stub: HelloWorldStub = HelloWorldGrpc.stub(channel)
    val greetingF: Future[Greeting] = stub.sayHello(toBeGreeted)
    greetingF.foreach(response => println(s"ASYNC RESULT: ${response.message}"))
    // beware: blocking code below
    val blockingStub: HelloWorldBlockingStub = HelloWorldGrpc.blockingStub(channel)
    val greeting: Greeting = blockingStub.sayHello(toBeGreeted)
    println(s"SYNC(BLOCKING) RESULT: ${greeting.message}")
  }

  run
}
