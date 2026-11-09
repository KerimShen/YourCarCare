package com.kerim.yourcarcare.logic;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.kerim.yourcarcare.model.VignetteType;

import org.junit.Test;

import java.time.LocalDate;

/** Tests for VignetteRules. 2026-11-06 is a Friday. */
public class VignetteRulesTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 9);

    @Test
    public void startExactly30DaysAhead_isAllowed() {
        assertFalse(VignetteRules.isStartTooFarAhead(TODAY.plusDays(30), TODAY));
    }

    @Test
    public void start31DaysAhead_isTooFar() {
        assertTrue(VignetteRules.isStartTooFarAhead(TODAY.plusDays(31), TODAY));
    }

    @Test
    public void startInThePast_isAllowed() {
        assertFalse(VignetteRules.isStartTooFarAhead(TODAY.minusDays(100), TODAY));
    }

    @Test
    public void weekend_canStartFridaySaturdayOrSunday() {
        assertTrue(VignetteRules.isValidStartDay(VignetteType.WEEKEND, LocalDate.of(2026, 11, 6)));
        assertTrue(VignetteRules.isValidStartDay(VignetteType.WEEKEND, LocalDate.of(2026, 11, 7)));
        assertTrue(VignetteRules.isValidStartDay(VignetteType.WEEKEND, LocalDate.of(2026, 11, 8)));
    }

    @Test
    public void weekend_cannotStartThursdayOrMonday() {
        assertFalse(VignetteRules.isValidStartDay(VignetteType.WEEKEND, LocalDate.of(2026, 11, 5)));
        assertFalse(VignetteRules.isValidStartDay(VignetteType.WEEKEND, LocalDate.of(2026, 11, 9)));
    }

    @Test
    public void otherTypes_canStartAnyDay() {
        assertTrue(VignetteRules.isValidStartDay(VignetteType.WEEKLY, LocalDate.of(2026, 11, 9)));
    }
}
