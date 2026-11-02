package com.kerim.yourcarcare.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kerim.yourcarcare.model.Vignette;

import java.util.List;

/** Database operations for vignettes. Call from a background thread. */
@Dao
public interface VignetteDao {

    @Insert
    long insert(Vignette vignette);

    @Update
    void update(Vignette vignette);

    @Delete
    void delete(Vignette vignette);

    @Query("SELECT * FROM vignettes WHERE id = :id")
    Vignette getById(long id);

    // Newest vignette first
    @Query("SELECT * FROM vignettes WHERE carId = :carId ORDER BY startDate DESC")
    List<Vignette> getForCar(long carId);
}
