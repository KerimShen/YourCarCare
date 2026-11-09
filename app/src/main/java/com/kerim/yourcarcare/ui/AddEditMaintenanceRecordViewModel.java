package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.CarRepository;
import com.kerim.yourcarcare.data.MaintenanceRecordRepository;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;

import java.time.LocalDate;

/** Data for AddEditMaintenanceRecordActivity. */
public class AddEditMaintenanceRecordViewModel extends AndroidViewModel {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository recordRepository;

    private LiveData<Car> car;
    private LiveData<MaintenanceRecord> record;

    // These live here (not in the activity) so they survive screen rotation
    boolean formFilled = false;
    LocalDate selectedDate = LocalDate.now();

    public AddEditMaintenanceRecordViewModel(@NonNull Application application) {
        super(application);
        carRepository = new CarRepository(application);
        recordRepository = new MaintenanceRecordRepository(application);
    }

    public LiveData<Car> getCar(long carId) {
        if (car == null) {
            car = carRepository.getById(carId);
        }
        return car;
    }

    public LiveData<MaintenanceRecord> getRecord(long recordId) {
        if (record == null) {
            record = recordRepository.getById(recordId);
        }
        return record;
    }

    /** Saves the record and raises the car's km if needed (see the repository). */
    public void save(MaintenanceRecord record) {
        recordRepository.save(record);
    }
}
