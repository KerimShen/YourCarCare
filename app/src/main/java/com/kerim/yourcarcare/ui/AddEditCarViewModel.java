package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.CarRepository;
import com.kerim.yourcarcare.model.Car;

/** Data for AddEditCarActivity. */
public class AddEditCarViewModel extends AndroidViewModel {

    private final CarRepository repository;
    private LiveData<Car> car;

    // True once the form has been filled (with the loaded car or with defaults).
    // The ViewModel survives rotation, so after rotating we don't overwrite what the user typed.
    boolean formFilled = false;

    public AddEditCarViewModel(@NonNull Application application) {
        super(application);
        repository = new CarRepository(application);
    }

    public LiveData<Car> getCar(long id) {
        if (car == null) {
            car = repository.getById(id);
        }
        return car;
    }

    public void save(Car car) {
        repository.save(car);
    }
}
