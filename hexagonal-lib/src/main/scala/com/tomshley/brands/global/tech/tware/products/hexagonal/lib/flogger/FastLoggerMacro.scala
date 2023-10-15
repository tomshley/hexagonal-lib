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

protected[flogger] object FastLoggerMacro {
  def log(
      fastLoggerSeverity: FastLoggerSeverity,
      fastLoggerSource: Option[FastLoggerSource] = Option.empty,
      fastLoggerMessage: Option[FastLoggerMessage] = Option.empty,
      fastLoggerException: Option[FastLoggerThrowable] = Option.empty
  ): Unit = {
    println(new FastLoggerMacro(
      Some(fastLoggerSeverity),
      fastLoggerSource,
      fastLoggerMessage,
      fastLoggerException
    )).toString
  }
}
final private[this] class FastLoggerMacro(
    val fastLoggerSeverity: Option[FastLoggerSeverity],
    val fastLoggerSource: Option[FastLoggerSource] = Option.empty,
    val fastLoggerMessage: Option[FastLoggerMessage] = Option.empty,
    val fastLoggerException: Option[FastLoggerThrowable] = Option.empty
) {
  override def toString: String = {
    def applyDashPrefix(string: String) = {
      Seq(" ", "-", " ", string).mkString("")
    }

    def applyBracketFormat(string: String) = {
      Seq(" ", "[", string, "]", " ").mkString("")
    }

    def applyChildScopeFormat(string: String) = {
      Seq(string, ":").mkString("")
    }

    import java.time.LocalDateTime
    import java.time.format.DateTimeFormatter
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm")

    lazy val logArguments: Seq[String] =
      Seq(
        Some(LocalDateTime.now()),
        fastLoggerSeverity,
        fastLoggerSource,
        fastLoggerMessage,
        fastLoggerException
      )
        .filter(
          _.isDefined
        )
        .map(
          _.get match
            case d: LocalDateTime      => d.format(formatter)
            case s: FastLoggerSeverity => applyBracketFormat(s.toLabel)
            case s: FastLoggerSource   => applyChildScopeFormat(
                s.clazz.asInstanceOf[AnyRef].getClass.toString
              )
            case m: FastLoggerMessage   => m.message
            case t: FastLoggerThrowable => applyDashPrefix(t.throwable.toString)
        )

    lazy val logPrintString: String = logArguments.mkString("")

    logPrintString
  }
}
