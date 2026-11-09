package com.kerim.yourcarcare.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.MaintenanceType;

import java.util.List;

/** Maintenance record data for the UI. Writes run on a background thread. */
public class MaintenanceRecordRepository {

    private final MaintenanceRecordDao recordDao;

    public MaintenanceRecordRepository(Application application) {
        recordDao = AppDatabase.getInstance(application).maintenanceRecordDao();
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
}
