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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.flogger

trait Flogger {
  @transient
  protected lazy val logger: FastLogger = FastLogger()
}

final class FastLogger private[flogger] () extends InlineFastLoggerMacro with Serializable

sealed trait InlineFastLoggerMacro {
  inline def debug(message: String): Unit = FastLoggerMacro
    .log(FastLoggerSeverity.DEBUG, fastLoggerMessage = Some(FastLoggerMessage(message)))
    .toString
  inline def debug(message: String, throwable: Throwable): Unit = FastLoggerMacro
    .log(
      FastLoggerSeverity.DEBUG,
      fastLoggerMessage = Some(FastLoggerMessage(message)),
      fastLoggerException = Some(FastLoggerThrowable(throwable))
    )
    .toString
  inline def error(message: String): Unit = FastLoggerMacro
    .log(FastLoggerSeverity.ERROR, fastLoggerMessage = Some(FastLoggerMessage(message)))
  inline def error(message: String, throwable: Throwable): Unit = FastLoggerMacro
    .log(
      FastLoggerSeverity.ERROR,
      fastLoggerMessage = Some(FastLoggerMessage(message)),
      fastLoggerException = Some(FastLoggerThrowable(throwable))
    )
  inline def info(message: String): Unit = FastLoggerMacro
    .log(FastLoggerSeverity.INFO, fastLoggerMessage = Some(FastLoggerMessage(message)))
  inline def info(message: String, throwable: Throwable): Unit = FastLoggerMacro
    .log(
      FastLoggerSeverity.INFO,
      fastLoggerMessage = Some(FastLoggerMessage(message)),
      fastLoggerException = Some(FastLoggerThrowable(throwable))
    )
  inline def trace(message: String): Unit = FastLoggerMacro
    .log(FastLoggerSeverity.TRACE, fastLoggerMessage = Some(FastLoggerMessage(message)))
  inline def trace(message: String, throwable: Throwable): Unit = FastLoggerMacro
    .log(
      FastLoggerSeverity.TRACE,
      fastLoggerMessage = Some(FastLoggerMessage(message)),
      fastLoggerException = Some(FastLoggerThrowable(throwable))
    )
  inline def warn(message: String): Unit = FastLoggerMacro
    .log(FastLoggerSeverity.WARN, fastLoggerMessage = Some(FastLoggerMessage(message)))
  inline def warn(message: String, throwable: Throwable): Unit = FastLoggerMacro
    .log(
      FastLoggerSeverity.WARN,
      fastLoggerMessage = Some(FastLoggerMessage(message)),
      fastLoggerException = Some(FastLoggerThrowable(throwable))
    )
}
