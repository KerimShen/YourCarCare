package com.kerim.yourcarcare.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.model.Car;

import java.util.List;

/**
 * The single place the UI goes to for car data.
 * Writes run on a background thread, so ViewModels can call them from the UI thread.
 */
public class CarRepository {

    private final CarDao carDao;

    public CarRepository(Application application) {
        carDao = AppDatabase.getInstance(application).carDao();
    }

    // Room already loads LiveData in the background, so these just pass through
    public LiveData<List<Car>> getAll() {
        return carDao.getAll();
    }

    public LiveData<Car> getById(long id) {
        return carDao.getById(id);
    }

    public void insert(Car car) {
        AppDatabase.databaseExecutor.execute(() -> carDao.insert(car));
    }

    public void update(Car car) {
        AppDatabase.databaseExecutor.execute(() -> carDao.update(car));
    }

    public void delete(Car car) {
        AppDatabase.databaseExecutor.execute(() -> carDao.delete(car));
    }

    /** Inserts a new car (id is still 0) or updates an existing one. */
    public void save(Car car) {
        if (car.id == 0) {
            insert(car);
        } else {
            update(car);
        }
    }
}
