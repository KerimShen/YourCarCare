package com.kerim.yourcarcare.ui;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/** Turns dates into text for the screen. */
public final class Dates {

    private Dates() {
    }

    /**
     * Formats a date the way the phone's language expects,
     * e.g. "Nov 7, 2026" in English or "7.11.2026 г." in Bulgarian.
     */
    public static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    // MaterialDatePicker works with milliseconds at midnight UTC, so we convert using UTC too.
    // (Using the phone's time zone here could shift the date by one day.)
    public static long toUtcMillis(LocalDate date) {
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static LocalDate fromUtcMillis(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
    }
}
