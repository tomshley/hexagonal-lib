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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaDateUtils {
    //~ Public Constants ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static final DateTimeFormatter DATE_FORMATTER;
    public static final DateTimeFormatter DATE_TIME_FORMATTER;
    public static final DateTimeFormatter TIME_FORMATTER;
    public static final DateTimeFormatter HTTP_COOKIE_DATE_TIME_FORMATTER;
    public static final DateTimeFormatter HTTP_HEADER_DATE_TIME_FORMATTER;
    private static final Logger log = LoggerFactory.getLogger(JavaDateUtils.class);

    //~ Initializers ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    static {
        DATE_FORMATTER = new DateTimeFormatterBuilder()
                .append(DateTimeFormat.forPattern("M/d/yyyy").getPrinter(), new DateTimeParser[]{
                        ISODateTimeFormat.localDateParser().getParser(),
                        DateTimeFormat.shortDate().getParser(),
                        DateTimeFormat.mediumDate().getParser(),
                        DateTimeFormat.longDate().getParser(),
                        DateTimeFormat.fullDate().getParser(),
                        DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
                        DateTimeFormat.forPattern("MM.dd.yyyy").getParser(),
                        DateTimeFormat.forPattern("MMM-dd-yyyy").getParser()
                })
                .toFormatter();
        TIME_FORMATTER = new DateTimeFormatterBuilder()
                .append(DateTimeFormat.forPattern("h:mma").getPrinter(), new DateTimeParser[]{
                        ISODateTimeFormat.timeParser().getParser(),
                        DateTimeFormat.shortTime().getParser(),
                        DateTimeFormat.mediumTime().getParser(),
                        DateTimeFormat.longTime().getParser(),
                        DateTimeFormat.fullTime().getParser(),
                        DateTimeFormat.forPattern("hh:mma").getParser(),
                        DateTimeFormat.forPattern("hh:mm a").getParser(),
                        DateTimeFormat.forPattern("hh:mma z").getParser(),
                        DateTimeFormat.forPattern("hh:mm a z").getParser(),
                        DateTimeFormat.forPattern("hha").getParser(),
                        DateTimeFormat.forPattern("hh a").getParser(),
                        DateTimeFormat.forPattern("HHmm").getParser(),
                        DateTimeFormat.forPattern("12 'NOON'").getParser()
                })
                .toFormatter();
        DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .append(ISODateTimeFormat.dateTimeNoMillis().getPrinter(), new DateTimeParser[]{
                        ISODateTimeFormat.dateTimeParser().getParser(),
                        null
                })
                .append(DateTimeFormat.shortDateTime().getPrinter(), new DateTimeParser[]{
                        DateTimeFormat.shortDateTime().getParser(),
                        null
                })
                .toFormatter();
        HTTP_COOKIE_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd-MMM-yyyy HH:mm:ss z");
        HTTP_HEADER_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss z");
    }

    //~ Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private JavaDateUtils() {
    }

    //~ Static Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static DateTime createDateTime(@Nullable final Object instant) {
        if (instant == null) {
            //noinspection ConstantConditions
            return null;
        }

        return new DateTime(instant);
    }

    public static boolean isAfter(final ReadableInstant date1, final ReadableInstant date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        return date1.isAfter(date2);
    }

    public static boolean isBefore(final ReadableInstant date1, final ReadableInstant date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        return date1.isBefore(date2);
    }

    public static <T extends ReadableInstant> T latest(final T date1, final T date2) {
        if (date1 == null) {
            return date2;
        }

        if (date2 == null) {
            return date1;
        }

        return date1.isAfter(date2) ? date1 : date2;
    }

    public static DateTime parseDateTime(@Nullable final String input) {
        if (input == null) {
            //noinspection ConstantConditions
            return null;
        }

        return DATE_TIME_FORMATTER.parseDateTime(input);
    }

    @NotNull
    public static DateTime parseDateTime(@Nullable final String input, @NotNull final DateTime defaultValue) {
        final DateTime value = parseDateTime(input);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public static LocalDate parseLocalDate(@Nullable final String input) {
        if (input == null) {
            //noinspection ConstantConditions
            return null;
        }

        return DATE_FORMATTER.parseDateTime(input).toLocalDate();
    }

    @NotNull
    public static LocalDate parseLocalDate(@Nullable final String input, @NotNull final LocalDate defaultValue) {
        final LocalDate value = parseLocalDate(input);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public static LocalTime parseLocalTime(@Nullable final String input) {
        if (input == null) {
            //noinspection ConstantConditions
            return null;
        }

        return TIME_FORMATTER.parseDateTime(input).toLocalTime();
    }

    @NotNull
    public static LocalTime parseLocalTime(@Nullable final String input, @NotNull final LocalTime defaultValue) {
        final LocalTime value = parseLocalTime(input);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    @NotNull
    public static String toFullString(@Nullable final LocalDate date) {
        if (date == null) {
            return "";
        }

        if (date.getYear() != new LocalDate().getYear()) {
            return date.toString(DateTimeFormat.fullDate());
        }

        return date.toString(DateTimeFormat.forPattern("EEEE, MMMM d"));
    }

    @NotNull
    public static String toString(@Nullable final LocalDate date) {
        if (date == null) {
            return "";
        }

        return date.toString(DATE_FORMATTER);
    }

    @NotNull
    public static String toString(@Nullable final LocalTime time) {
        if (time == null) {
            return "";
        }

        return time.toString(TIME_FORMATTER).toLowerCase();
    }

    @Nullable
    public static DateTime tryParseDateTime(@Nullable final String input) {
        try {
            return parseDateTime(input);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to parse DateTime: {}", e.getMessage());
            return null;
        }
    }

    @Nullable
    public static LocalDate tryParseLocalDate(@Nullable final String input) {
        try {
            return parseLocalDate(input);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to parse LocalDate: {}", e.getMessage());
            return null;
        }
    }

    @Nullable
    public static LocalTime tryParseLocalTime(@Nullable final String input) {
        try {
            return parseLocalTime(input);
        } catch (IllegalArgumentException e) {
            log.warn("Unable to parse LocalTime: {}", e.getMessage());
            return null;
        }
    }

}
