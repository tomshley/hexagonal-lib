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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.simplelogger

trait Flogger {
  @transient
  protected lazy val logger: SimpleLogger = SimpleLogger()
}

final class SimpleLogger private[simplelogger]() extends InlineSimpleLoggerMacro with Serializable

sealed trait InlineSimpleLoggerMacro {
  inline def debug(message: String): Unit = SimpleLoggerMacro
    .log(SimpleLoggerSeverity.DEBUG, simpleLoggerMessage = Some(SimpleLoggerMessage(message)))
    .toString
  inline def debug(message: String, throwable: Throwable): Unit = SimpleLoggerMacro
    .log(
      SimpleLoggerSeverity.DEBUG,
      simpleLoggerMessage = Some(SimpleLoggerMessage(message)),
      simpleLoggerException = Some(SimpleLoggerThrowable(throwable))
    )
    .toString
  inline def error(message: String): Unit = SimpleLoggerMacro
    .log(SimpleLoggerSeverity.ERROR, simpleLoggerMessage = Some(SimpleLoggerMessage(message)))
  inline def error(message: String, throwable: Throwable): Unit = SimpleLoggerMacro
    .log(
      SimpleLoggerSeverity.ERROR,
      simpleLoggerMessage = Some(SimpleLoggerMessage(message)),
      simpleLoggerException = Some(SimpleLoggerThrowable(throwable))
    )
  inline def info(message: String): Unit = SimpleLoggerMacro
    .log(SimpleLoggerSeverity.INFO, simpleLoggerMessage = Some(SimpleLoggerMessage(message)))
  inline def info(message: String, throwable: Throwable): Unit = SimpleLoggerMacro
    .log(
      SimpleLoggerSeverity.INFO,
      simpleLoggerMessage = Some(SimpleLoggerMessage(message)),
      simpleLoggerException = Some(SimpleLoggerThrowable(throwable))
    )
  inline def trace(message: String): Unit = SimpleLoggerMacro
    .log(SimpleLoggerSeverity.TRACE, simpleLoggerMessage = Some(SimpleLoggerMessage(message)))
  inline def trace(message: String, throwable: Throwable): Unit = SimpleLoggerMacro
    .log(
      SimpleLoggerSeverity.TRACE,
      simpleLoggerMessage = Some(SimpleLoggerMessage(message)),
      simpleLoggerException = Some(SimpleLoggerThrowable(throwable))
    )
  inline def warn(message: String): Unit = SimpleLoggerMacro
    .log(SimpleLoggerSeverity.WARN, simpleLoggerMessage = Some(SimpleLoggerMessage(message)))
  inline def warn(message: String, throwable: Throwable): Unit = SimpleLoggerMacro
    .log(
      SimpleLoggerSeverity.WARN,
      simpleLoggerMessage = Some(SimpleLoggerMessage(message)),
      simpleLoggerException = Some(SimpleLoggerThrowable(throwable))
    )
}
