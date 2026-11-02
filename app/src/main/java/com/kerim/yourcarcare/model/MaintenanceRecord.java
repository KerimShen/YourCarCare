package com.kerim.yourcarcare.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/** One service job done on a car (e.g. an oil change at 120 000 km). */
@Entity(
        tableName = "maintenance_records",
        // Same as Vignette: deleting a car also deletes its maintenance records
        foreignKeys = @ForeignKey(
                entity = Car.class,
                parentColumns = "id",
                childColumns = "carId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("carId"))
public class MaintenanceRecord {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long carId;

    public MaintenanceType maintenanceType;

    public LocalDate date;

    // Odometer reading when the job was done
    public int km;

    // Optional free text, may be null
    public String note;

    public MaintenanceRecord() {
    }
}
