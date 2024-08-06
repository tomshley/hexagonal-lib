/*
 * copyright 2023 tomshley llc
 *
 * licensed under the apache license, version 2.0 (the "license");
 * you may not use this file except in compliance with the license.
 * you may obtain a copy of the license at
 *
 * http://www.apache.org/licenses/license-2.0
 *
 * unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "as is" basis,
 * without warranties or conditions of any kind, either express or implied.
 * see the license for the specific language governing permissions and
 * limitations under the license.
 *
 * @author thomas schena @sgoggles <https://github.com/sgoggles> | <https://gitlab.com/sgoggles>
 *
 */

package com.tomshley.hexagonal.http2

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.{Http, model}

import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GrpcServerBoilerplate
    extends Http2Boilerplate[
      model.HttpRequest => scala.concurrent.Future[model.HttpResponse]
    ] {
  override def start(
    interface: String,
    port: Int,
    system: ActorSystem[?],
    service: model.HttpRequest => scala.concurrent.Future[model.HttpResponse]
  ): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem[?] = system
    implicit val ec: ExecutionContext =
      system.executionContext

    val bound =
      Http()
        .newServerAt(interface, port)
        .bind(service)
        .map(_.addToCoordinatedShutdown(3.seconds))

    bound.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info(
          "Rpc Server online at gRPC app {}:{}",
          address.getHostString,
          address.getPort
        )
      case Failure(ex) =>
        system.log.error("Failed to bind gRPC endpoint, terminating system", ex)
        system.terminate()
    }
    bound
  }

}
