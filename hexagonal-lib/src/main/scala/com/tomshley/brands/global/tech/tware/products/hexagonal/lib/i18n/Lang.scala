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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib
package i18n

import java.util.Locale

case class Lang(locale: Locale) {
  def language: String = locale.getLanguage

  def country: String = locale.getCountry
}

object Lang {
  val Default: Lang = Lang(Locale.getDefault)

  def apply(maybeLang: Option[String], default: Lang = Default): Lang = maybeLang.map(apply) getOrElse default

  def apply(language: String): Lang = Lang(new Locale(language))
}
