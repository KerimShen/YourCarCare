package com.kerim.yourcarcare.logic;

import com.kerim.yourcarcare.model.VignetteType;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** Validation rules for a vignette's start date. Plain Java, so it can be unit tested. */
public final class VignetteRules {

    /** A vignette can be bought at most this many days before it starts. */
    public static final int MAX_DAYS_AHEAD = 30;

    private VignetteRules() {
    }

    /** True if the start date is more than MAX_DAYS_AHEAD days after today. Past dates are fine. */
    public static boolean isStartTooFarAhead(LocalDate start, LocalDate today) {
        return start.isAfter(today.plusDays(MAX_DAYS_AHEAD));
    }

    /** A weekend vignette must start on Friday, Saturday or Sunday. Other types can start any day. */
    public static boolean isValidStartDay(VignetteType type, LocalDate start) {
        if (type != VignetteType.WEEKEND) {
            return true;
        }
        DayOfWeek day = start.getDayOfWeek();
        return day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
