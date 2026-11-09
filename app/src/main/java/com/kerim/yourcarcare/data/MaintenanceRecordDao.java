package com.kerim.yourcarcare.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.MaintenanceType;

import java.util.List;

/** Database operations for maintenance records. Non-LiveData methods must run on a background thread. */
@Dao
public interface MaintenanceRecordDao {

    @Insert
    long insert(MaintenanceRecord record);

    @Update
    void update(MaintenanceRecord record);

    @Delete
    void delete(MaintenanceRecord record);

    @Query("SELECT * FROM maintenance_records WHERE id = :id")
    LiveData<MaintenanceRecord> getById(long id);

    // Full service history of a car, newest first
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId ORDER BY date DESC")
    LiveData<List<MaintenanceRecord>> getForCar(long carId);

    // Only one kind of job, e.g. all oil changes of a car
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId AND maintenanceType = :type "
            + "ORDER BY date DESC")
    LiveData<List<MaintenanceRecord>> getForCarAndType(long carId, MaintenanceType type);

    // The most recent job of this kind, or null if it was never done.
    // Useful later for "when is the next oil change due?"
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId AND maintenanceType = :type "
            + "ORDER BY date DESC LIMIT 1")
    MaintenanceRecord getLatestOfType(long carId, MaintenanceType type);
}
