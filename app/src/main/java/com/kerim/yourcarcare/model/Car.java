package com.kerim.yourcarcare.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** A car owned by the user. Each object is one row in the "cars" table. */
@Entity(tableName = "cars")
public class Car {

    // autoGenerate = true: Room picks the next free id when we insert a new car
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String brand = "";

    @NonNull
    public String model = "";

    public int year;

    public EngineType engineType;

    public boolean isTurbo;

    public Transmission transmission;

    public int currentKm;

    public DrivingStyle drivingStyle;

    // Room needs an empty constructor to create objects when reading from the database
    public Car() {
    }
}
