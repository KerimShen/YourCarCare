package com.kerim.yourcarcare.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.MaintenanceType;

import java.util.List;

/** Maintenance record data for the UI. Writes run on a background thread. */
public class MaintenanceRecordRepository {

    private final AppDatabase database;
    private final MaintenanceRecordDao recordDao;
    private final CarDao carDao;

    public MaintenanceRecordRepository(Application application) {
        database = AppDatabase.getInstance(application);
        recordDao = database.maintenanceRecordDao();
        carDao = database.carDao();
    }

    public LiveData<MaintenanceRecord> getById(long id) {
        return recordDao.getById(id);
    }

    public LiveData<List<MaintenanceRecord>> getForCar(long carId) {
        return recordDao.getForCar(carId);
    }

    public LiveData<List<MaintenanceRecord>> getForCarAndType(long carId, MaintenanceType type) {
        return recordDao.getForCarAndType(carId, type);
    }

    public void insert(MaintenanceRecord record) {
        AppDatabase.databaseExecutor.execute(() -> recordDao.insert(record));
    }

    public void update(MaintenanceRecord record) {
        AppDatabase.databaseExecutor.execute(() -> recordDao.update(record));
    }

    public void delete(MaintenanceRecord record) {
        AppDatabase.databaseExecutor.execute(() -> recordDao.delete(record));
    }

    /**
     * Inserts (id 0) or updates the record. If its km is higher than the car's
     * current km, the car's km is raised to match.
     */
    public void save(MaintenanceRecord record) {
        AppDatabase.databaseExecutor.execute(() ->
                // A transaction: either both writes happen, or (on an error) neither does
                database.runInTransaction(() -> {
                    if (record.id == 0) {
                        recordDao.insert(record);
                    } else {
                        recordDao.update(record);
                    }
                    carDao.raiseCurrentKm(record.carId, record.km);
                }));
    }
}
