package com.kerim.yourcarcare.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kerim.yourcarcare.model.Car;

import java.util.List;

/**
 * Database operations for cars. Room writes the implementation for us.
 * Methods returning LiveData run in the background automatically.
 * The other methods block, so call them through CarRepository, not from the UI thread.
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

    // LiveData: observers are notified again every time the cars table changes
    @Query("SELECT * FROM cars ORDER BY brand, model")
    LiveData<List<Car>> getAll();

    // Emits null if there is no car with this id
    @Query("SELECT * FROM cars WHERE id = :id")
    LiveData<Car> getById(long id);
}
