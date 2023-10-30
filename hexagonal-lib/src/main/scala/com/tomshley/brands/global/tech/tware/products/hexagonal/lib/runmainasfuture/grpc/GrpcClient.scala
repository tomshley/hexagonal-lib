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

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.runmainasfuture.core.RunMainFutureSugar

import scala.concurrent.Future

// nb: this import is mandated by the addServices method
import io.grpc.{ManagedChannel, ManagedChannelBuilder}

trait GrpcClient extends RunMainFutureSugar[ManagedChannel, _] {
  override lazy val serverCreation: Future[ManagedChannel] = {
    val channel = ManagedChannelBuilder
      .forAddress(serverProperties.hostname, serverProperties.port)
      .usePlaintext() // don't use encryption (for demo purposes)
      .build

    channelStubs(channel)

    Future(channel)
  }

  def channelStubs(channel: ManagedChannel): Unit
}
