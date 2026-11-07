package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.CarRepository;
import com.kerim.yourcarcare.data.MaintenanceRecordRepository;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;

import java.util.List;

/** Data for CarDetailActivity: one car and its maintenance history. */
public class CarDetailViewModel extends AndroidViewModel {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository recordRepository;

    private LiveData<Car> car;
    private LiveData<List<MaintenanceRecord>> records;

    public CarDetailViewModel(@NonNull Application application) {
        super(application);
        carRepository = new CarRepository(application);
        recordRepository = new MaintenanceRecordRepository(application);
    }

    /** Call once from onCreate. After a rotation the data is already loaded, so we skip it. */
    public void init(long carId) {
        if (car == null) {
            car = carRepository.getById(carId);
            records = recordRepository.getForCar(carId);
        }
    }

    public LiveData<Car> getCar() {
        return car;
    }

    public LiveData<List<MaintenanceRecord>> getRecords() {
        return records;
    }

    public void deleteCar(Car car) {
        carRepository.delete(car);
    }

    public void deleteRecord(MaintenanceRecord record) {
        recordRepository.delete(record);
    }
}
