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

import com.tomshley.brands.global.tech.tware.products.hexagonal.lib.config.ConfigKeys

import java.util.{Locale, ResourceBundle}

class UTF8BundleControl extends ResourceBundle.Control {
  val Format = "properties.utf8"

  override def getFormats(baseName: String): java.util.List[String] = {
    import scala.jdk.CollectionConverters.*

    Seq(Format).asJava
  }

  override def getFallbackLocale(baseName: String, locale: Locale) =
    if locale == Locale.getDefault then null
    else Locale.getDefault

  override def newBundle(baseName: String, locale: Locale, fmt: String, loader: ClassLoader, reload: Boolean): ResourceBundle = {
    import java.io.InputStreamReader
    import java.util.PropertyResourceBundle

    // The below is an approximate copy of the default Java implementation
    def resourceName = {
      lazy val defaultExt = ConfigKeys.I18N_DEFAULT_FILE_EXT.toValue
      toResourceName(toBundleName(baseName, locale), defaultExt)
    }

    def stream =
      if reload then {
        for
          url <- Option(loader getResource resourceName)
          connection <- Option(url.openConnection)
        yield {
          connection.setUseCaches(false)
          connection.getInputStream
        }
      } else
        Option(loader getResourceAsStream resourceName)

    (for
      format <- Option(fmt) if format == Format
      is <- stream
    yield new PropertyResourceBundle(new InputStreamReader(is, "UTF-8"))).orNull
  }
}
