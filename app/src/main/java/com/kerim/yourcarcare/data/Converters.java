package com.kerim.yourcarcare.data;

import androidx.room.TypeConverter;

import java.time.LocalDate;

/**
 * SQLite has no date type, so Room needs help storing LocalDate.
 * We save a date as the number of days since 1970-01-01 (a Long).
 * Numbers also sort correctly, so "ORDER BY date" works in queries.
 */
public class Converters {

    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }

    @TypeConverter
    public static LocalDate toLocalDate(Long epochDay) {
        return epochDay == null ? null : LocalDate.ofEpochDay(epochDay);
    }
}
