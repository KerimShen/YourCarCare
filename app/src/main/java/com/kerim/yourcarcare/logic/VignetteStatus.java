package com.kerim.yourcarcare.logic;

import com.kerim.yourcarcare.model.Vignette;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Works out which vignette matters right now and how many days it has left.
 * Plain Java (no Android classes), so it can be tested with normal JUnit tests.
 */
public final class VignetteStatus {

    public enum State {
        VALID,          // valid today, more than EXPIRING_SOON_DAYS left
        EXPIRING_SOON,  // valid today, EXPIRING_SOON_DAYS or fewer left
        UPCOMING,       // starts in the future
        EXPIRED,        // all vignettes are in the past
        NONE            // the car has no vignettes at all
    }

    public static final int EXPIRING_SOON_DAYS = 3;

    public final State state;
    public final Vignette vignette; // null when state is NONE
    public final long daysLeft;     // only meaningful for VALID and EXPIRING_SOON (0 = expires today)

    private VignetteStatus(State state, Vignette vignette, long daysLeft) {
        this.state = state;
        this.vignette = vignette;
        this.daysLeft = daysLeft;
    }

    /**
     * Picks the vignette to show, in this order:
     * 1. one that is valid today (if several, the one that lasts longest)
     * 2. otherwise the next upcoming one (earliest start)
     * 3. otherwise the most recently expired one
     * "today" is a parameter so tests can use a fixed date.
     */
    public static VignetteStatus of(List<Vignette> vignettes, LocalDate today) {
        Vignette active = null;
        Vignette upcoming = null;
        Vignette expired = null;

        for (Vignette v : vignettes) {
            LocalDate expiry = v.getExpiryDate();
            if (expiry == null) {
                continue; // missing type or start date, ignore it
            }
            if (v.startDate.isAfter(today)) {
                if (upcoming == null || v.startDate.isBefore(upcoming.startDate)) {
                    upcoming = v;
                }
            } else if (expiry.isBefore(today)) {
                if (expired == null || expiry.isAfter(expired.getExpiryDate())) {
                    expired = v;
                }
            } else {
                // start <= today <= expiry
                if (active == null || expiry.isAfter(active.getExpiryDate())) {
                    active = v;
                }
            }
        }

        if (active != null) {
            long daysLeft = ChronoUnit.DAYS.between(today, active.getExpiryDate());
            State state = daysLeft <= EXPIRING_SOON_DAYS ? State.EXPIRING_SOON : State.VALID;
            return new VignetteStatus(state, active, daysLeft);
        }
        if (upcoming != null) {
            return new VignetteStatus(State.UPCOMING, upcoming, 0);
        }
        if (expired != null) {
            return new VignetteStatus(State.EXPIRED, expired, 0);
        }
        return new VignetteStatus(State.NONE, null, 0);
    }
}
