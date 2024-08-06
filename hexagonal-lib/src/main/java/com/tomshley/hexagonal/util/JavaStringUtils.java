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

package com.tomshley.hexagonal.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

public final class JavaStringUtils {

    //~ Public Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    public static final String ARROW_DOWN = "▼";
    @NotNull
    public static final String ARROW_LEFT = "◄";
    @NotNull
    public static final String ARROW_RIGHT = "►";
    @NotNull
    public static final String ARROW_UP = "▲";
    @NotNull
    public static final String BULLET = "●";
    @NotNull
    public static final String ELLIPSIS = "…";

    //~ Static Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @NotNull
    private static final Pattern identifierPattern = Pattern.compile("(?:(?<=\\p{javaLowerCase})(?=\\p{javaUpperCase}))|_");
    @NotNull
    private static final Pattern namePattern = Pattern.compile("\\p{javaLetterOrDigit}");
    @NotNull
    private static final Pattern nonLettersPattern = Pattern.compile("[^\\p{L}\\p{Nd}\\p{Zs}\\p{Po}\\p{Pd}\\p{Pc}\\p{Sm}]");
    @NotNull
    private static final HashMap<Integer, String> numberStrings;

    //~ Initializers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static {
        numberStrings = new HashMap<Integer, String>(20);
        numberStrings.put(0, "None");
        numberStrings.put(1, "One");
        numberStrings.put(2, "Two");
        numberStrings.put(3, "Three");
        numberStrings.put(4, "Four");
        numberStrings.put(5, "Five");
        numberStrings.put(6, "Six");
        numberStrings.put(7, "Seven");
        numberStrings.put(8, "Eight");
        numberStrings.put(9, "Nine");
        numberStrings.put(10, "Ten");
        numberStrings.put(11, "Eleven");
        numberStrings.put(12, "Twelve");
        numberStrings.put(13, "Thirteen");
        numberStrings.put(14, "Fourteen");
        numberStrings.put(15, "Fifteen");
        numberStrings.put(16, "Sixteen");
        numberStrings.put(17, "Seventeen");
        numberStrings.put(18, "Eighteen");
        numberStrings.put(19, "Nineteen");
    }

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private JavaStringUtils() {
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static String abbreviate(@Nullable final CharSequence input, final int maxLength) {
        return abbreviate(input, maxLength, null);
    }

    public static String abbreviate(@Nullable final CharSequence input, final int maxLength, @Nullable final MutableBoolean valueChanged) {
        if (input == null || input.length() <= maxLength || input.length() <= ELLIPSIS.length()) {
            if (valueChanged != null) {
                valueChanged.setValue(false);
            }

            return toString(input);
        }

        int endIndex = maxLength - ELLIPSIS.length();

        boolean foundWhitespace = Character.isWhitespace(input.charAt(endIndex));
        while (endIndex > 0) {
            if (Character.isWhitespace(input.charAt(endIndex - 1))) {
                foundWhitespace = true;
            } else if (foundWhitespace) {
                break;
            }

            endIndex--;
        }

        if (endIndex == 0) { // Deal with input that is a single, unbroken word
            endIndex = maxLength - ELLIPSIS.length();
        }

        if (valueChanged != null) {
            valueChanged.setValue(true);
        }

        return input.subSequence(0, endIndex) + ELLIPSIS;
    }

    public static String append(@Nullable final String value, @Nullable final String suffix) {
        if (isEmpty(value) || isEmpty(suffix)) {
            return value;
        }

        return value + suffix;
    }

    public static <T extends CharSequence & Appendable> T append(@Nullable final T value, @Nullable final CharSequence suffix) {
        return append(value, suffix, null);
    }

    public static String append(@Nullable final String value, @Nullable final String suffix, @NotNull final CharSequence separator) {
        if (isEmpty(value) || isEmpty(suffix)) {
            return value;
        }

        return value + separator + suffix;
    }

    public static <T extends CharSequence & Appendable> T append(@Nullable final T value,
                                                                 @Nullable final CharSequence suffix,
                                                                 @NotNull final CharSequence separator) {
        if (isEmpty(value) || isEmpty(suffix)) {
            return value;
        }

        try {
            if (!isEmpty(separator)) {
                value.append(separator);
            }

            value.append(suffix);
        } catch (IOException ignore) {
        }

        return value;
    }

    public static String appendAll(@Nullable final String value, @Nullable final String... suffixs) {
        if (isEmpty(value) || suffixs == null) {
            return value;
        }

        final StringBuilder sb = new StringBuilder(value);
        for (final String curSuffix : suffixs) {
            if (curSuffix != null) {
                sb.append(curSuffix);
            }
        }

        if (sb.length() == value.length()) {
            // Avoid the extra work sb.toString() has to do if we can
            return value;
        }

        return sb.toString();
    }

    public static <T extends CharSequence & Appendable> T appendAll(@Nullable final T value, @Nullable final CharSequence... suffixs) {
        if (isEmpty(value) || suffixs == null) {
            return value;
        }

        for (final CharSequence curSuffix : suffixs) {
            if (!isEmpty(curSuffix)) {
                try {
                    value.append(curSuffix);
                } catch (IOException ignore) {
                }
            }
        }

        return value;
    }

    public static String appendAll(@Nullable final String value, @Nullable final String suffix1, @Nullable final String suffix2) {
        if (isEmpty(value)) {
            return value;
        }

        if (suffix1 == null) {
            return append(value, suffix2);
        } else if (suffix2 == null) {
            return append(value, suffix1);
        }

        if (suffix1.length() == 0 && suffix2.length() == 0) {
            return value;
        }

        return value + suffix1 + suffix2;
    }

    public static <T extends CharSequence & Appendable> T appendAll(@Nullable final T value,
                                                                    @Nullable final CharSequence suffix1,
                                                                    @Nullable final CharSequence suffix2) {
        if (isEmpty(value)) {
            return value;
        }

        if (suffix1 == null) {
            return append(value, suffix2);
        } else if (suffix2 == null) {
            return append(value, suffix1);
        }

        if (suffix1.length() == 0 && suffix2.length() == 0) {
            return value;
        }

        try {
            value.append(suffix1).append(suffix2);
        } catch (IOException ignored) {
        }

        return value;
    }

    public static String coalesce(@Nullable final Object... values) {
        if (values == null) {
            return null;
        }

        for (final Object curValue : values) {
            if (curValue == null) {
                continue;
            }

            final String curString = curValue.toString();
            if (StringUtils.isNotEmpty(curString)) {
                return curString;
            }
        }

        return null;
    }

    public static String coalesce(@Nullable final String value1, @Nullable final String value2) {
        if (StringUtils.isNotEmpty(value1)) {
            return value1;
        }

        if (StringUtils.isNotEmpty(value2)) {
            return value2;
        }

        return null;
    }

    public static String coalesce(@Nullable final String value1, @Nullable final String value2, @Nullable final String value3) {
        if (StringUtils.isNotEmpty(value1)) {
            return value1;
        }

        if (StringUtils.isNotEmpty(value2)) {
            return value2;
        }

        if (StringUtils.isNotEmpty(value3)) {
            return value3;
        }

        return null;
    }

    public static String coalesceBlank(@Nullable final Object... values) {
        if (values == null) {
            return null;
        }

        for (final Object curValue : values) {
            if (curValue == null) {
                continue;
            }

            final String curString = curValue.toString();
            if (StringUtils.isNotBlank(curString)) {
                return curString;
            }
        }

        return null;
    }

    public static String coalesceBlank(@Nullable final String value1, @Nullable final String value2) {
        if (StringUtils.isNotBlank(value1)) {
            return value1;
        }

        if (StringUtils.isNotBlank(value2)) {
            return value2;
        }

        return null;
    }

    public static String coalesceBlank(@Nullable final String value1, @Nullable final String value2, @Nullable final String value3) {
        if (StringUtils.isNotBlank(value1)) {
            return value1;
        }

        if (StringUtils.isNotBlank(value2)) {
            return value2;
        }

        if (StringUtils.isNotBlank(value3)) {
            return value3;
        }

        return null;
    }

    public static StringBuilder collapseWhitespace(@Nullable final String value) {
        return collapseWhitespace(value, " ");
    }

    public static StringBuilder collapseWhitespace(@Nullable final String value, @NotNull final CharSequence replacement) {
        if (value == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder(value.length());

        boolean lastWhitespace = false;
        for (int i = 0; i < value.length(); i++) {
            final int curCodePoint = value.codePointAt(i);

            if (Character.isWhitespace(curCodePoint)) {
                if (sb.length() > 0) {
                    lastWhitespace = true;
                }

                continue;
            }

            if (lastWhitespace) {
                lastWhitespace = false;
                sb.append(replacement);
            }

            sb.appendCodePoint(curCodePoint);
        }

        return sb;
    }

    public static String concat(@Nullable final Object... values) {
        if (values == null) {
            return null;
        }

        return StringUtils.join(values);
    }

    public static boolean contains(@Nullable final CharSequence value, final char searchChar) {
        return indexOf(value, searchChar) >= 0;
    }

    public static String convertIdentifier(@Nullable final String value) {
        return convertIdentifier(value, " ");
    }

    public static String convertIdentifier(@Nullable final String value, @NotNull final CharSequence separator) {
        if (isEmpty(value)) {
            return value;
        }

        return WordUtils.capitalizeFully(JavaStringUtils.identifierPattern.matcher(value).replaceAll(separator.toString()));
    }

    public static String convertToIdentifier(@Nullable final String value) {
        return convertToIdentifier(value, "");
    }

    public static String convertToIdentifier(@Nullable final String value, @NotNull final CharSequence replacement) {
        if (value == null) {
            return null;
        }

        final StringBuilder sb = new StringBuilder(value.length());

        for (int i = 0; i < value.length(); i++) {
            final int curCodePoint = value.codePointAt(i);

            if (sb.length() == 0 && Character.isJavaIdentifierStart(curCodePoint)) {
                sb.appendCodePoint(curCodePoint);
            } else if (Character.isJavaIdentifierPart(curCodePoint)) {
                sb.appendCodePoint(curCodePoint);
            } else {
                sb.append(replacement);
            }
        }

        return sb.toString();
    }

    @NotNull
    public static String convertToNumberString(@Nullable final Integer value) {
        if (value == null) {
            return "";
        }

        final String numberString = numberStrings.get(value);
        if (numberString != null) {
            return numberString;
        }

        return String.format("%,d", value);
    }

    /**
     * Get the plural form of a word.  This method uses a fairly simple
     * algoritm and only works for English input.  Most irregular plural forms are not
     * correctly converted.  You should only use this method if you have a constrained
     * set of tested inputs.
     *
     * @param word word to make plural
     * @return the plural form of <code>word</code>
     * @see #convertToSingular
     */
    public static String convertToPlural(@Nullable final String word) {
        if (word == null || word.length() < 2) {
            return word;
        }

        final char[] lastChars = {word.charAt(word.length() - 1), word.charAt(word.length() - 2)};

        if ("sxz".indexOf(lastChars[0]) != -1) {
            return word + "es";
        }

        if (lastChars[0] == 'h' && "aeioudgkprt".indexOf(lastChars[1]) == -1) {
            return word + "es";
        }

        if (lastChars[0] == 'y' && "aeiou".indexOf(lastChars[1]) == -1) {
            return word.substring(0, word.length() - 1) + "ies";
        }

        if (word.endsWith("Person")) {
            return word.substring(0, word.length() - 6) + "People";
        }

        return word + "s";
    }

    public static String convertToPlural(@Nullable final String value, final int count) {
        if (count == 1) {
            return value;
        }

        return convertToPlural(value);
    }

    /**
     * Get the singular form of a word.  This method uses a fairly simple
     * algoritm and only works for English input.  Most irregular plural forms are not
     * correctly converted.  You should only use this method if you have a constrained
     * set of tested inputs.
     *
     * @param word word to make singular
     * @return the singular form of <code>word</code>
     * @see #convertToPlural
     */
    public static String convertToSingular(@Nullable final String word) {
        if (word == null || word.length() < 2) {
            return word;
        }

        if (word.endsWith("es")) {
            final char[] lastChars = {word.charAt(word.length() - 3), word.charAt(word.length() - 4)};

            if (lastChars[0] == 'i' && "aeiou".indexOf(lastChars[1]) == -1) {
                return word.substring(0, word.length() - 3) + "y";
            }

            if (lastChars[0] == 'h' && "aeioudgkprt".indexOf(lastChars[1]) == -1) {
                return word.substring(0, word.length() - 2);
            }

            if ("sxz".indexOf(lastChars[0]) != -1) {
                return word.substring(0, word.length() - 2);
            }
        }

        if (word.endsWith("People")) {
            return word.substring(0, word.length() - 6) + "Person";
        }

        if (word.endsWith("s")) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }

    /**
     * Decode a string in the "x-www-form-urlencoded" form, enhanced with the UTF-8-in-URL proposal.
     *
     * @param url The string to be decoded
     * @return The decoded string
     * @see #encodeUrl
     * @see <a href="http://www.w3.org/International/unescape.java">Original Source</a>
     */
    public static String decodeUrl(@Nullable final CharSequence url) {
        if (url == null) {
            return null;
        }

        return URLUTF8Encoder.decode(url);
    }

    /**
     * Encode a string to the "x-www-form-urlencoded" form, enhanced with the UTF-8-in-URL proposal. This is what happens:
     * <ul>
     * <li>The ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same.</li>
     * <li>The unreserved characters - _ . ! ~ * ' ( ) remain the same.</li>
     * <li>The space character ' ' is converted into a plus sign '+'.</li>
     * <li>All other ASCII characters are converted into the 3-character string "%xy", where xy is the two-digit
     * hexadecimal representation of the character code</li>
     * <li>All non-ASCII characters are encoded in two steps: first to a sequence of 2 or 3 bytes, using the UTF-8 algorithm; secondly each of these bytes is encoded as "%xx".</li>
     * </ul>
     *
     * @param value The string to be encoded
     * @return The encoded string
     * @see #decodeUrl
     * @see <a href="http://www.w3.org/International/URLUTF8Encoder.java">Original Source</a>
     */
    @NotNull
    public static String encodeUrl(@Nullable final CharSequence value) {
        if (value == null) {
            return "";
        }

        return URLUTF8Encoder.encode(value);
    }

    public static String ensureEndsWith(@Nullable final String value, @Nullable final String suffix) {
        if (isEmpty(value) || suffix == null || value.endsWith(suffix)) {
            return value;
        }

        return value + suffix;
    }

    public static String ensureStartsWith(@Nullable final String value, @Nullable final String prefix) {
        if (isEmpty(value) || prefix == null || value.startsWith(prefix)) {
            return value;
        }

        return prefix + value;
    }

    public static String ensureSurroundedWith(@Nullable final String value, @Nullable String prefix, @Nullable String suffix) {
        if (isEmpty(value)) {
            return value;
        }

        if (prefix == null) {
            prefix = "";
        }

        if (suffix == null) {
            suffix = "";
        }

        final StringBuilder sb = new StringBuilder(value.length() + prefix.length() + suffix.length());

        if (!value.startsWith(prefix)) {
            sb.append(prefix);
        }

        sb.append(value);

        if (!value.endsWith(suffix)) {
            sb.append(suffix);
        }

        return sb.toString();
    }

    public static void ensureSurroundedWith(@NotNull final StringBuilder sb,
                                            @Nullable final String value,
                                            @Nullable final String prefix,
                                            @Nullable final String suffix) {
        if (isEmpty(value)) {
            return;
        }

        if (prefix != null && !value.startsWith(prefix)) {
            sb.append(prefix);
        }

        sb.append(value);

        if (suffix != null && !value.endsWith(suffix)) {
            sb.append(suffix);
        }
    }

    public static boolean equalsAny(@Nullable final String input, @Nullable final String... values) {
        if (values == null || values.length == 0) {
            return false;
        }

        if (input == null) {
            for (final String curValue : values) {
                if (curValue == null) {
                    return true;
                }
            }
        } else {
            for (final String curValue : values) {
                if (input.equals(curValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean equalsAnyIgnoreCase(@Nullable final String input, @Nullable final String... values) {
        if (values == null || values.length == 0) {
            return false;
        }

        if (input == null) {
            for (final String curValue : values) {
                if (curValue == null) {
                    return true;
                }
            }
        } else {
            for (final String curValue : values) {
                if (input.equalsIgnoreCase(curValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static int indexOf(@Nullable final CharSequence value, final char searchChar) {
        if (value == null) {
            return -1;
        }

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == searchChar) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isBlank(@Nullable final CharSequence value) {
        if (value == null) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isEmpty(@Nullable final CharSequence value) {
        return value == null || value.length() == 0;
    }

    @NotNull
    public static String join(@NotNull final String separator, @Nullable final Object... values) {
        if (values == null || values.length == 0) {
            return "";
        }

        if (values.length == 1) {
            return toString(values[0], "");
        }

        final StringBuilder sb = new StringBuilder(256);
        for (final Object curValue : values) {
            if (curValue == null) {
                continue;
            }

            final String curString = curValue.toString();
            if (isEmpty(curString)) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(separator);
            }

            sb.append(curString);
        }

        return sb.toString();
    }

    @NotNull
    public static String join(@NotNull final String separator, @Nullable final Iterable<?> values) {
        if (values == null) {
            return "";
        }

        final Iterator<?> iterator = values.iterator();
        if (iterator == null) {
            return "";
        }

        final StringBuilder sb = new StringBuilder(256);
        while (iterator.hasNext()) {
            final Object curValue = iterator.next();
            if (curValue == null) {
                continue;
            }

            final String curString = curValue.toString();
            if (isEmpty(curString)) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append(separator);
            }

            sb.append(curString);
        }

        return sb.toString();
    }

    public static String joinToList(@Nullable final Object... values) {
        if (values == null || values.length == 0) {
            return null;
        }

        if (values.length == 1) {
            return values[0] != null ? values[0].toString() : null;
        }

        String lastString = null;

        final StringBuilder sb = new StringBuilder(256);
        for (final Object curValue : values) {
            if (curValue == null) {
                continue;
            }

            final String curString = curValue.toString();
            if (isEmpty(curString)) {
                continue;
            }

            if (lastString != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(lastString);
            }

            lastString = curString;
        }

        if (lastString != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }

            sb.append(lastString);
        }

        return sb.toString();
    }

    public static String joinToList(@Nullable final Iterable<?> values) {
        if (values == null) {
            return null;
        }

        final Iterator<?> iterator = values.iterator();
        if (iterator == null) {
            return null;
        }

        String lastString = null;

        final StringBuilder sb = new StringBuilder(256);
        while (iterator.hasNext()) {
            final Object curValue = iterator.next();
            final String curString = curValue.toString();
            if (isEmpty(curString)) {
                continue;
            }

            if (lastString != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }

                sb.append(lastString);
            }

            lastString = curString;
        }

        if (lastString != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }

            sb.append(lastString);
        }

        return sb.toString();
    }

    public static boolean matches(@Nullable final String reference, @Nullable final String test) {
        return matches(reference, test, 0);
    }

    public static boolean matches(@Nullable String reference, @Nullable String test, final int maxLevenshteinDistance) {
        if (reference == null) {
            return test == null;
        }

        if (test == null) {
            return false;
        }

        if (reference.length() == test.length()) {
            return reference.equalsIgnoreCase(test);
        }

        reference = namePattern.matcher(reference).replaceAll("");
        test = namePattern.matcher(test).replaceAll("");

        if (maxLevenshteinDistance <= 0) {
            return reference.equalsIgnoreCase(test);
        }

        return StringUtils.getLevenshteinDistance(reference.toLowerCase(), test.toLowerCase()) <= maxLevenshteinDistance;
    }

    public static String prepend(@Nullable final String value, @Nullable final String prefix) {
        if (isEmpty(value) || prefix == null) {
            return value;
        }

        return prefix + value;
    }

    public static StringBuilder prepend(@Nullable final StringBuilder value, @Nullable final String prefix) {
        if (value == null || value.length() == 0 || prefix == null) {
            return value;
        }

        return value.insert(0, prefix);
    }

    public static String quote(@Nullable final String input) {
        return quote(input, '\"');
    }

    public static String quote(@Nullable final String input, final char quoteChar) {
        if (input == null) {
            return null;
        }

        return quoteChar + input + quoteChar;
    }

    public static String removeSymbols(@Nullable final String input) {
        return JavaStringUtils.nonLettersPattern.matcher(input).replaceAll("");
    }

    public static boolean startsWith(@Nullable final CharSequence value, @Nullable final CharSequence test) {
        if (value == null) {
            return false;
        }

        if (test == null) {
            return true;
        }

        if (value.length() < test.length()) {
            return false;
        }

        return test.equals(value.subSequence(0, test.length()));
    }

    @NotNull
    public static String stripToEmpty(@Nullable final String value, @Nullable final String stripChars) {
        final String newValue = StringUtils.strip(value, stripChars);
        if (newValue == null) {
            return "";
        }

        return newValue;
    }

    @Nullable
    public static String stripToNull(@Nullable final Object value, @Nullable final String stripChars) {
        if (value == null) {
            return null;
        }

        return stripToNull(value.toString(), stripChars);
    }

    @Nullable
    public static String stripToNull(@Nullable final String value, @Nullable final String stripChars) {
        final String newValue = StringUtils.strip(value, stripChars);
        if ("".equals(newValue)) {
            return null;
        }

        return newValue;
    }

    public static String surround(@Nullable final String value, @Nullable String prefix, @Nullable String suffix) {
        if (isEmpty(value)) {
            return value;
        }

        if (prefix == null) {
            prefix = "";
        }

        if (suffix == null) {
            suffix = "";
        }

        String sb = prefix +
                value +
                suffix;

        return sb;
    }

    public static String toString(@Nullable final Object value) {
        return toString(value, null);
    }

    public static String toString(@Nullable final Double value) {
        return toString(value, null);
    }

    public static String toString(@Nullable final Object value, @Nullable final String defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        final String valueString = value.toString();
        return valueString != null ? valueString : "";
    }

    public static String toString(@Nullable final Double value, @Nullable final String defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        final String result = value.toString();
        if (result.endsWith(".0")) {
            return result.substring(0, result.length() - 2);
        }

        return result;
    }

    public static String toTitleCase(@Nullable final String input) {
        if (input == null) {
            return null;
        }

        String outputString = WordUtils.capitalizeFully(input);

        // TODO: See if there is a more inefficient way to do this :)
        outputString = outputString.replace(" A ", " a ");
        outputString = outputString.replace(" Of ", " of ");
        outputString = outputString.replace(" De ", " de ");
        outputString = outputString.replace(" Del ", " del ");
        outputString = outputString.replace(" The ", " the ");
        outputString = outputString.replace(" Los ", " los ");
        outputString = outputString.replace(" Las ", " las ");

        return outputString;
    }

    public static String truncate(@Nullable final String input, final int maxLength) {
        return truncate(input, maxLength, null);
    }

    public static String truncate(@Nullable final String input, final int maxLength, final MutableBoolean wasTruncated) {
        if (maxLength <= 0 || input == null || input.length() < maxLength) {
            if (wasTruncated != null) {
                wasTruncated.setValue(false);
            }

            return input;
        }

        if (wasTruncated != null) {
            wasTruncated.setValue(true);
        }
        return StringUtils.stripEnd(input.substring(0, maxLength), null) + ELLIPSIS;
    }

    @Nullable
    public static Integer tryParseInteger(@Nullable String input) {
        input = StringUtils.stripToNull(input);
        if (input == null) {
            return null;
        }

        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Integer[] tryParseIntegers(@Nullable final String[] values) {
        if (values == null) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }

        final List<Integer> intValues = new ArrayList<Integer>(values.length);
        for (final String value : values) {
            final Integer curIntValue = tryParseInteger(value);
            if (curIntValue != null) {
                intValues.add(curIntValue);
            }
        }

        return intValues.toArray(new Integer[intValues.size()]);
    }

    @Nullable
    public static Long tryParseLong(@Nullable String input) {
        input = StringUtils.stripToNull(input);
        if (input == null) {
            return null;
        }

        try {
            return Long.valueOf(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Float tryParseFloat(@Nullable final String input) {
        if (isEmpty(input)) {
            return null;
        }

        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static URI tryParseURI(@Nullable String input) {
        input = StringUtils.stripToNull(input);
        if (input == null) {
            return null;
        }

        try {
            return new URI(input);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Nullable
    public static UUID tryParseUUID(@Nullable final String input) {
        if (isEmpty(input)) {
            return null;
        }

        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static String unqualify(@Nullable final String name) {
        if (name == null) {
            return null;
        }

        final int index = name.lastIndexOf('.');
        if (index < 0) {
            return name;
        }

        return name.substring(index + 1);
    }

    //~ Inner Classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Provides a method to encode any string into a URL-safe form. Non-ASCII characters are first encoded as sequences of
     * two or three bytes, using the UTF-8 algorithm, before being encoded as %HH escapes.
     * <p/>
     * Created: 17 April 1997
     * <p/>
     * Copyright © 1997 World Wide Web Consortium, (Massachusetts Institute of Technology, European Research Consortium for
     * Informatics and Mathematics, Keio University). All Rights Reserved. This work is distributed under the
     * <a href="http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231">W3C® Software License</a> in the hope
     * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
     * FOR A PARTICULAR PURPOSE.
     *
     * @author Bert Bos <bert@w3.org>
     * @see <a href="http://www.w3.org/International/URLUTF8Encoder.java">URLUTF8Encoder</a>
     * @see <a href="http://www.w3.org/International/unescape.java">unescape</a>
     */
    @SuppressWarnings({"StandardVariableNames"})
    private static class URLUTF8Encoder {

        //~ Static Fields ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        private static final String[] hex = {
                "%00", "%01", "%02", "%03", "%04", "%05", "%06", "%07",
                "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e", "%0f",
                "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
                "%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f",
                "%20", "%21", "%22", "%23", "%24", "%25", "%26", "%27",
                "%28", "%29", "%2a", "%2b", "%2c", "%2d", "%2e", "%2f",
                "%30", "%31", "%32", "%33", "%34", "%35", "%36", "%37",
                "%38", "%39", "%3a", "%3b", "%3c", "%3d", "%3e", "%3f",
                "%40", "%41", "%42", "%43", "%44", "%45", "%46", "%47",
                "%48", "%49", "%4a", "%4b", "%4c", "%4d", "%4e", "%4f",
                "%50", "%51", "%52", "%53", "%54", "%55", "%56", "%57",
                "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
                "%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67",
                "%68", "%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f",
                "%70", "%71", "%72", "%73", "%74", "%75", "%76", "%77",
                "%78", "%79", "%7a", "%7b", "%7c", "%7d", "%7e", "%7f",
                "%80", "%81", "%82", "%83", "%84", "%85", "%86", "%87",
                "%88", "%89", "%8a", "%8b", "%8c", "%8d", "%8e", "%8f",
                "%90", "%91", "%92", "%93", "%94", "%95", "%96", "%97",
                "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e", "%9f",
                "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
                "%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af",
                "%b0", "%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7",
                "%b8", "%b9", "%ba", "%bb", "%bc", "%bd", "%be", "%bf",
                "%c0", "%c1", "%c2", "%c3", "%c4", "%c5", "%c6", "%c7",
                "%c8", "%c9", "%ca", "%cb", "%cc", "%cd", "%ce", "%cf",
                "%d0", "%d1", "%d2", "%d3", "%d4", "%d5", "%d6", "%d7",
                "%d8", "%d9", "%da", "%db", "%dc", "%dd", "%de", "%df",
                "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6", "%e7",
                "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
                "%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7",
                "%f8", "%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff"
        };

        //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        private URLUTF8Encoder() {
        }

        //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        /**
         * Decode a string in the "x-www-form-urlencoded" form, enhanced with the UTF-8-in-URL proposal.
         *
         * @param s The string to be decoded
         * @return The decoded string
         * @see #encode
         * @see <a href="http://www.w3.org/International/unescape.java">Original Source</a>
         */
        public static String decode(final CharSequence s) {
            final StringBuilder sb = new StringBuilder();
            final int l = s.length();
            int ch;
            int b, sum_b = 0;
            for (int i = 0, more = -1; i < l; i++) {
                /* Get next byte b from URL segment s */
                switch (ch = s.charAt(i)) {
                    case '%':
                        ch = s.charAt(++i);
                        final int hb = (Character.isDigit((char) ch)
                                ? ch - '0'
                                : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                        ch = s.charAt(++i);
                        final int lb = (Character.isDigit((char) ch)
                                ? ch - '0'
                                : 10 + Character.toLowerCase((char) ch) - 'a') & 0xF;
                        b = (hb << 4) | lb;
                        break;
                    case '+':
                        b = ' ';
                        break;
                    default:
                        b = ch;
                }
                /* Decode byte b as UTF-8, sum_b collects incomplete chars */
                if ((b & 0xc0) == 0x80) {// 10xxxxxx (continuation byte)
                    sum_b = (sum_b << 6) | (b & 0x3f);// Add 6 bits to sum_b
                    if (--more == 0) {
                        sb.append((char) sum_b);
                    }
                } else if ((b & 0x80) == 0x00) {// 0xxxxxxx (yields 7 bits)
                    sb.append((char) b);
                } else if ((b & 0xe0) == 0xc0) {// 110xxxxx (yields 5 bits)
                    sum_b = b & 0x1f;
                    more = 1;// Expect 1 more byte
                } else if ((b & 0xf0) == 0xe0) {// 1110xxxx (yields 4 bits)
                    sum_b = b & 0x0f;
                    more = 2;// Expect 2 more bytes
                } else if ((b & 0xf8) == 0xf0) {// 11110xxx (yields 3 bits)
                    sum_b = b & 0x07;
                    more = 3;// Expect 3 more bytes
                } else if ((b & 0xfc) == 0xf8) {// 111110xx (yields 2 bits)
                    sum_b = b & 0x03;
                    more = 4;// Expect 4 more bytes
                } else
                    /*if ((b & 0xfe) == 0xfc)*/ {// 1111110x (yields 1 bit)
                    sum_b = b & 0x01;
                    more = 5;// Expect 5 more bytes
                }
                /* We don't test if the UTF-8 encoding is well-formed */
            }
            return sb.toString();
        }

        /**
         * Encode a string to the "x-www-form-urlencoded" form, enhanced with the UTF-8-in-URL proposal. This is what happens:
         * <ul>
         * <li>The ASCII characters 'a' through 'z', 'A' through 'Z', and '0' through '9' remain the same.</li>
         * <li>The unreserved characters - _ . ! ~ * ' ( ) remain the same.</li>
         * <li>The space character ' ' is converted into a plus sign '+'.</li>
         * <li>All other ASCII characters are converted into the 3-character string "%xy", where xy is the two-digit
         * hexadecimal representation of the character code</li>
         * <li>All non-ASCII characters are encoded in two steps: first to a sequence of 2 or 3 bytes, using the UTF-8
         * algorithm; secondly each of these bytes is encoded as "%xx".</li>
         * </ul>
         *
         * @param s The string to be encoded
         * @return The encoded string
         * @see #encode
         * @see <a href="http://www.w3.org/International/URLUTF8Encoder.java">Original Source</a>
         */
        public static String encode(final CharSequence s) {
            final StringBuilder sbuf = new StringBuilder();
            final int len = s.length();
            for (int i = 0; i < len; i++) {
                final int ch = s.charAt(i);
                if ('A' <= ch && ch <= 'Z') {// 'A'..'Z'
                    sbuf.append((char) ch);
                } else if ('a' <= ch && ch <= 'z') {// 'a'..'z'
                    sbuf.append((char) ch);
                } else if ('0' <= ch && ch <= '9') {// '0'..'9'
                    sbuf.append((char) ch);
                } else if (ch == ' ') {// space
                    sbuf.append('+');
                } else if (ch == '-' || ch == '_'// unreserved
                        || ch == '.' || ch == '!'
                        || ch == '~' || ch == '*'
                        || ch == '\'' || ch == '('
                        || ch == ')') {
                    sbuf.append((char) ch);
                } else if (ch <= 0x007f) {// other ASCII
                    sbuf.append(hex[ch]);
                } else if (ch <= 0x07FF) {// non-ASCII <= 0x7FF
                    sbuf.append(hex[0xc0 | (ch >> 6)]);
                    sbuf.append(hex[0x80 | (ch & 0x3F)]);
                } else {// 0x7FF < ch <= 0xFFFF
                    sbuf.append(hex[0xe0 | (ch >> 12)]);
                    sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
                    sbuf.append(hex[0x80 | (ch & 0x3F)]);
                }
            }
            return sbuf.toString();
        }
    }

}
