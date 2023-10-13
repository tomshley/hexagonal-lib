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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.i18n

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.basics.PimpedType
import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.config.ConfigKeys

import java.text.MessageFormat
import java.util.ResourceBundle

sealed trait LocalizedStringType extends PimpedType[String] {
  private lazy val defaultName = ConfigKeys.I18N_DEFAULT_NAME.toValue
  val underlying: String

  def apply(underlying: String, args: Any*)(implicit lang: Lang): String = {
    (new MessageFormat(raw(underlying), lang.locale).format(args.map(_.asInstanceOf[java.lang.Object]).toArray)).toString
  }

  def raw(message: String)(implicit lang: Lang): String = {
    val bundle = ResourceBundle.getBundle(defaultName, lang.locale)
    bundle.getString(message)
  }
}

final class LocalizedString(val underlying: String) extends LocalizedStringType
