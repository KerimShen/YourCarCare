package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.CarRepository;
import com.kerim.yourcarcare.model.Car;

import java.util.List;

/**
 * Data for CarListActivity. A ViewModel survives screen rotation,
 * so the car list isn't reloaded every time the screen turns.
 * AndroidViewModel (instead of ViewModel) gives us the Application for the repository.
 */
public class CarListViewModel extends AndroidViewModel {

    private final CarRepository repository;
    private final LiveData<List<Car>> cars;

    public CarListViewModel(@NonNull Application application) {
        super(application);
        repository = new CarRepository(application);
        cars = repository.getAll();
    }

    public LiveData<List<Car>> getCars() {
        return cars;
    }

    public void delete(Car car) {
        repository.delete(car);
    }
}
