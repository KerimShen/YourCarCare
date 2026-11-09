package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.CarRepository;
import com.kerim.yourcarcare.data.MaintenanceRecordRepository;
import com.kerim.yourcarcare.data.VignetteRepository;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.Vignette;

import java.util.List;

/** Data for CarDetailActivity: one car, its vignettes and its maintenance history. */
public class CarDetailViewModel extends AndroidViewModel {

    private final CarRepository carRepository;
    private final MaintenanceRecordRepository recordRepository;

    private final VignetteRepository vignetteRepository;

    private LiveData<Car> car;
    private LiveData<List<MaintenanceRecord>> records;
    private LiveData<List<Vignette>> vignettes;

    // Is the vignette history list open? Kept here so it stays open after a rotation.
    boolean vignetteHistoryExpanded = false;

    public CarDetailViewModel(@NonNull Application application) {
        super(application);
        carRepository = new CarRepository(application);
        recordRepository = new MaintenanceRecordRepository(application);
        vignetteRepository = new VignetteRepository(application);
    }

    /** Call once from onCreate. After a rotation the data is already loaded, so we skip it. */
    public void init(long carId) {
        if (car == null) {
            car = carRepository.getById(carId);
            records = recordRepository.getForCar(carId);
            vignettes = vignetteRepository.getForCar(carId);
        }
    }

    public LiveData<List<Vignette>> getVignettes() {
        return vignettes;
    }

    public void deleteVignette(Vignette vignette) {
        vignetteRepository.delete(vignette);
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
