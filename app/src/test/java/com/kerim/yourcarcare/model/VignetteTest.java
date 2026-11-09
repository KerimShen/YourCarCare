package com.kerim.yourcarcare.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;

/** Tests for Vignette.getExpiryDate(). 2026-11-06 is a Friday. */
public class VignetteTest {

    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 6);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 11, 7);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 11, 8);

    private static Vignette vignette(VignetteType type, LocalDate start) {
        Vignette v = new Vignette();
        v.type = type;
        v.startDate = start;
        return v;
    }

    @Test
    public void daily_expiresOnStartDay() {
        assertEquals(FRIDAY, vignette(VignetteType.DAILY, FRIDAY).getExpiryDate());
    }

    @Test
    public void weekend_startingFriday_expiresSunday() {
        assertEquals(SUNDAY, vignette(VignetteType.WEEKEND, FRIDAY).getExpiryDate());
    }

    @Test
    public void weekend_startingSaturday_expiresSunday() {
        assertEquals(SUNDAY, vignette(VignetteType.WEEKEND, SATURDAY).getExpiryDate());
    }

    @Test
    public void weekend_startingSunday_expiresSameDay() {
        assertEquals(SUNDAY, vignette(VignetteType.WEEKEND, SUNDAY).getExpiryDate());
    }

    @Test
    public void weekly_lastsSevenDays() {
        assertEquals(LocalDate.of(2026, 11, 12), vignette(VignetteType.WEEKLY, FRIDAY).getExpiryDate());
    }

    @Test
    public void monthly_endsDayBeforeSameDateNextMonth() {
        assertEquals(LocalDate.of(2026, 12, 5), vignette(VignetteType.MONTHLY, FRIDAY).getExpiryDate());
    }

    @Test
    public void yearly_endsDayBeforeSameDateNextYear() {
        assertEquals(LocalDate.of(2027, 11, 5), vignette(VignetteType.YEARLY, FRIDAY).getExpiryDate());
    }
}
