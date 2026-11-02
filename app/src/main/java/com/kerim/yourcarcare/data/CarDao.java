package com.kerim.yourcarcare.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kerim.yourcarcare.model.Car;

import java.util.List;

/**
 * Database operations for cars. Room writes the implementation for us.
 * Note: these methods block, so call them from a background thread, not the UI thread.
 */
@Dao
public interface CarDao {

    // Returns the id Room generated for the new car
    @Insert
    long insert(Car car);

    @Update
    void update(Car car);

    // Also deletes the car's vignettes and maintenance records (ForeignKey.CASCADE)
    @Delete
    void delete(Car car);

    @Query("SELECT * FROM cars ORDER BY brand, model")
    List<Car> getAll();

    // Returns null if there is no car with this id
    @Query("SELECT * FROM cars WHERE id = :id")
    Car getById(long id);
}
