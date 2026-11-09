package com.kerim.yourcarcare.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import com.kerim.yourcarcare.model.Vignette;
import com.kerim.yourcarcare.model.VignetteType;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

/** Tests for VignetteStatus.of(). "Today" is always 2026-11-09 so results don't depend on the real date. */
public class VignetteStatusTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 9);

    private static Vignette vignette(VignetteType type, LocalDate start) {
        Vignette v = new Vignette();
        v.type = type;
        v.startDate = start;
        return v;
    }

    @Test
    public void noVignettes_isNone() {
        VignetteStatus status = VignetteStatus.of(Collections.emptyList(), TODAY);
        assertEquals(VignetteStatus.State.NONE, status.state);
        assertNull(status.vignette);
    }

    @Test
    public void activeWithTenDaysLeft_isValid() {
        // Monthly from 2026-10-20 ends 2026-11-19 -> 10 days after today
        Vignette v = vignette(VignetteType.MONTHLY, LocalDate.of(2026, 10, 20));
        VignetteStatus status = VignetteStatus.of(Collections.singletonList(v), TODAY);
        assertEquals(VignetteStatus.State.VALID, status.state);
        assertEquals(10, status.daysLeft);
    }

    @Test
    public void activeWithThreeDaysLeft_isExpiringSoon() {
        // Weekly from 2026-11-06 ends 2026-11-12 -> 3 days after today
        Vignette v = vignette(VignetteType.WEEKLY, LocalDate.of(2026, 11, 6));
        VignetteStatus status = VignetteStatus.of(Collections.singletonList(v), TODAY);
        assertEquals(VignetteStatus.State.EXPIRING_SOON, status.state);
        assertEquals(3, status.daysLeft);
    }

    @Test
    public void activeEndingToday_isExpiringSoonWithZeroDays() {
        Vignette v = vignette(VignetteType.DAILY, TODAY);
        VignetteStatus status = VignetteStatus.of(Collections.singletonList(v), TODAY);
        assertEquals(VignetteStatus.State.EXPIRING_SOON, status.state);
        assertEquals(0, status.daysLeft);
    }

    @Test
    public void onlyFutureVignettes_earliestIsUpcoming() {
        Vignette later = vignette(VignetteType.YEARLY, LocalDate.of(2026, 12, 1));
        Vignette sooner = vignette(VignetteType.WEEKLY, LocalDate.of(2026, 11, 20));
        VignetteStatus status = VignetteStatus.of(Arrays.asList(later, sooner), TODAY);
        assertEquals(VignetteStatus.State.UPCOMING, status.state);
        assertSame(sooner, status.vignette);
    }

    @Test
    public void onlyPastVignettes_latestExpiredIsShown() {
        Vignette older = vignette(VignetteType.WEEKLY, LocalDate.of(2026, 9, 1));
        Vignette newer = vignette(VignetteType.MONTHLY, LocalDate.of(2026, 10, 1)); // ends 10-31
        VignetteStatus status = VignetteStatus.of(Arrays.asList(older, newer), TODAY);
        assertEquals(VignetteStatus.State.EXPIRED, status.state);
        assertSame(newer, status.vignette);
    }

    @Test
    public void activeWinsOverUpcomingAndExpired() {
        Vignette expired = vignette(VignetteType.WEEKLY, LocalDate.of(2026, 10, 1));
        Vignette upcoming = vignette(VignetteType.YEARLY, LocalDate.of(2026, 12, 1));
        Vignette active = vignette(VignetteType.MONTHLY, LocalDate.of(2026, 10, 20));
        VignetteStatus status = VignetteStatus.of(Arrays.asList(expired, upcoming, active), TODAY);
        assertSame(active, status.vignette);
    }
}
