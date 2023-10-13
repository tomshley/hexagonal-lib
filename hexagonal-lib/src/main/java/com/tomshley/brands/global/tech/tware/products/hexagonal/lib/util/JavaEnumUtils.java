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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.regex.Pattern;

public final class JavaEnumUtils {

    //~ Static Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    private static final Pattern enumSetSplitPattern = Pattern.compile("[,;\\s]");

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private JavaEnumUtils() {
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Converts the values of an EnumSet to a int bitmask
     *
     * @param set The EnumSet to convert
     * @return A int value with each bit set according to the enum ordering
     */
    public static <T extends Enum<T>> int createIntBitmask(@Nullable final EnumSet<T> set) {
        if (set == null) {
            return 0;
        }

        int flags = 0;
        for (final T curValue : set) {
            flags |= 1L << curValue.ordinal();
        }

        return flags;
    }

    /**
     * Converts the values of an EnumSet to a long bitmask
     *
     * @param set The EnumSet to convert
     * @return A long value with each bit set according to the enum ordering
     */
    public static <T extends Enum<T>> long createLongBitmask(@Nullable final EnumSet<T> set) {
        if (set == null) {
            return 0;
        }

        long flags = 0;
        for (final T curValue : set) {
            flags |= 1L << curValue.ordinal();
        }

        return flags;
    }

    @NotNull
    public static <T extends Enum<T>> T getByOrdinal(@Nullable final Integer ordinal, @NotNull final T defaultValue) {
        if (ordinal == null) {
            return defaultValue;
        }

        final T[] values = defaultValue.getDeclaringClass().getEnumConstants();
        if (ordinal < 0 || ordinal >= values.length) {
            return defaultValue;
        }

        return values[ordinal];
    }

    @NotNull
    public static <T extends Enum<T>> EnumSet<T> parseEnumSet(@NotNull final Class<T> enumClass, @Nullable final String names) {
        final EnumSet<T> set = EnumSet.noneOf(enumClass);

        if (StringUtils.isEmpty(names)) {
            return set;
        }

        for (final String curName : enumSetSplitPattern.split(names)) {
            final T curValue = tryParse(enumClass, curName);
            if (curValue != null) {
                set.add(curValue);
            }
        }

        return set;
    }

    /**
     * Extracts the bits from a long value and converts to a set of Enums
     *
     * @param enumClass The Enum class to use
     * @param flags     The raw bitmask
     * @return A bitmask representing the
     */
    @NotNull
    public static <T extends Enum<T>> EnumSet<T> toEnumSet(@NotNull final Class<T> enumClass, final long flags) {
        final EnumSet<T> set = EnumSet.noneOf(enumClass);

        long mask = 1;
        for (final T curValue : enumClass.getEnumConstants()) {
            if ((mask & flags) == mask) {
                set.add(curValue);
            }

            mask <<= 1;
        }

        return set;
    }

    @NotNull
    public static <T extends Enum<T>> T tryParse(@Nullable final String name, @NotNull final T defaultValue) {
        return tryParse(defaultValue.getDeclaringClass(), name, defaultValue);
    }

    @Nullable
    public static <T extends Enum<T>> T tryParse(@NotNull final Class<T> enumClass, @Nullable String name) {
        name = StringUtils.stripToNull(name);
        if (name == null) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @NotNull
    public static <T extends Enum<T>> T tryParse(@NotNull final Class<T> enumClass, @Nullable final String name, @NotNull final T defaultValue) {
        final T value = tryParse(enumClass, name);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    @Nullable
    public static <T extends Enum<T>> T tryParseAsIndex(@NotNull final Class<T> enumClass, @Nullable final String indexString) {
        final Integer index = JavaStringUtils.tryParseInteger(indexString);
        if (index == null || index < 0) {
            return null;
        }

        final T[] values = enumClass.getEnumConstants();
        if (index > values.length) {
            return null;
        }

        return values[index];
    }
}

