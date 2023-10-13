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

trait Localized[T] {
  def locale(a: T): Lang
}

object Localized {
  def apply[T: Localized, R](a: T)(fn: Lang => R): R = {
    val localized = implicitly[Localized[T]]
    fn(localized.locale(a))
  }

  implicit def optionLocalized[T](implicit localized: Localized[T]): Localized[Option[T]] =
    new Localized[Option[T]] {
      override def locale(o: Option[T]): Lang =
        o map (localized.locale) getOrElse Lang.Default
    }
}
