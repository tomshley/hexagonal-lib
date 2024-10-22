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
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Directives.{complete, concat, get, path}
import org.apache.pekko.http.scaladsl.server.Route

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object WebServerBoilerplate extends Http2Boilerplate[Seq[Route]] {

  override def start(interface: String,
                     port: Int,
                     system: ActorSystem[?],
                     binding: Seq[Route]): Future[Http.ServerBinding] = {
    implicit val sys: ActorSystem[?] = system
    implicit val ec: ExecutionContext =
      system.executionContext

    val bindingFuture =
      Http()
        .newServerAt(interface, port)
        .bind(concat(Seq(get {
          path("heartbeat") {
            complete(Instant.now().toString)
          }
        }) ++ binding: _*))

    println(
      s"Server now online. Please navigate to http://$interface:$port/home\nPress RETURN to stop..."
    )
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
    bindingFuture
  }
}
