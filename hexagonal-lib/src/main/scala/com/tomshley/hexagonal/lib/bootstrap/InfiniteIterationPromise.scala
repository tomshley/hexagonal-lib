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

package com.tomshley.hexagonal.lib.bootstrap

import org.apache.pekko.actor.typed.ActorSystem

import scala.concurrent.{ExecutionContext, Future, Promise}

object InfiniteIterationPromise {
  def apply(system: ActorSystem[?], body: => Unit): Future[Boolean] = {
    given ec:ExecutionContext = system.executionContext

    def processLoopIteration(body: => Unit): Future[Boolean] = {
      body

      // For readability
      val preventNextProcess = false
      Future(preventNextProcess)
    }
    
    def promiseIteration(body: => Unit): Future[Boolean] = {
      val promise = Promise[Boolean]()
      promise.completeWith(processLoopIteration(body))

      promise.future.flatMap((preventNextProcess: Boolean) => {
        if preventNextProcess then
          Future(true)
        else
          promiseIteration(body)
      })
    }

    promiseIteration {
      body
    }
  }
}
