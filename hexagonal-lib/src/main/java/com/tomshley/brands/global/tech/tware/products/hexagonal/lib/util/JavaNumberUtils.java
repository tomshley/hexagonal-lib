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

package com.tomshley.brands.global.tech.tware.products.hexagonal.lib.util;

import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Locale;

public final class JavaNumberUtils {

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private JavaNumberUtils() {
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static int compare(final int l1, final int l2) {
        return l1 < l2 ? -1 : l1 == l2 ? 0 : 1;
    }

    public static int compare(final long l1, final long l2) {
        return l1 < l2 ? -1 : l1 == l2 ? 0 : 1;
    }

    public static int defaultNumber(@Nullable final Integer value, final int defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static long defaultNumber(@Nullable final Long value, final long defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String toString(final long value, final String singularLabel) {
        // TODO: i18n
        return toString(value, singularLabel, JavaStringUtils.convertToPlural(singularLabel));
    }

    public static String toString(final long value, final String singularLabel, final String pluralLabel) {
        // TODO: i18n
        if (value == 0) {
            return "No " + pluralLabel;
        }

        return NumberFormat.getIntegerInstance(Locale.getDefault()).format(value) + " " + (value == 1 ? singularLabel : pluralLabel);
    }
}
