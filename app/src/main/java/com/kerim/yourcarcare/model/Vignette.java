package com.kerim.yourcarcare.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * A road toll vignette bought for a car.
 * The expiry date is not stored; it is calculated from the type and start date.
 */
@Entity(
        tableName = "vignettes",
        // carId must match the id of an existing car.
        // CASCADE: when that car is deleted, its vignettes are deleted too.
        foreignKeys = @ForeignKey(
                entity = Car.class,
                parentColumns = "id",
                childColumns = "carId",
                onDelete = ForeignKey.CASCADE),
        // An index makes "find all vignettes of car X" fast (Room warns without it)
        indices = @Index("carId"))
public class Vignette {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long carId;

    public VignetteType type;

    public LocalDate startDate;

    public Vignette() {
    }

    /**
     * Returns the last day the vignette is valid (inclusive).
     * Example: a weekly vignette starting on Monday is valid until Sunday.
     * There is no field for this, so Room does not create a column for it.
     */
    public LocalDate getExpiryDate() {
        if (type == null || startDate == null) {
            return null;
        }
        switch (type) {
            case DAILY:
                return startDate;               // valid only on the start day
            case WEEKEND:
                // Valid until Sunday 23:59. nextOrSame gives the start date itself
                // if it is already Sunday, otherwise the next Sunday (Fri/Sat -> Sun).
                return startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case WEEKLY:
                return startDate.plusDays(6);
            case MONTHLY:
                return startDate.plusMonths(1).minusDays(1);
            case QUARTERLY:
                return startDate.plusMonths(3).minusDays(1);
            case YEARLY:
                return startDate.plusYears(1).minusDays(1);
            default:
                return startDate;
        }
    }

    /** True if today is after the last valid day. */
    public boolean isExpired() {
        LocalDate expiry = getExpiryDate();
        return expiry != null && LocalDate.now().isAfter(expiry);
    }
}
