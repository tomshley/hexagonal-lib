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

package com.tomshley.hexagonal.lib

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.management.cluster.bootstrap.ClusterBootstrap
import org.apache.pekko.management.scaladsl.PekkoManagement
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

object ManagedClusterService {
  def apply(serviceName:String, body: (system:ActorSystem[?]) => Unit): Unit = {
    def logger: Logger = LoggerFactory.getLogger(s"$serviceName-ManagedClusterService")

    ActorSystem[Nothing](Behaviors.setup[Nothing] { context =>
      try {
        bootstrap(context.system)
        body(context.system)
      } catch {
        case NonFatal(e) =>
          logger.error("Terminating due to initialization failure.", e)
          context.system.terminate()
      }

      Behaviors.empty
    }, serviceName)

  }

  private def bootstrap(system: ActorSystem[?]): Unit = {
    PekkoManagement(system).start()
    ClusterBootstrap(system).start()
  }
}
