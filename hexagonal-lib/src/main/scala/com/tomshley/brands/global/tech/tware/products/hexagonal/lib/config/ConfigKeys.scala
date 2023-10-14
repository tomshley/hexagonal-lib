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
package config

enum ConfigKeys(configBlockKey: ConfigBlockKey) {
  case I18N_DEFAULT_NAME extends ConfigKeys(ConfigBlockKey(ConfigBlocks.I18N, "defaultName", Some("strings")))
  case I18N_DEFAULT_FILE_EXT extends ConfigKeys(ConfigBlockKey(ConfigBlocks.I18N, "defaultExt", Some(".i18n")))

  def toValue: String = configBlockKey.toString
}
