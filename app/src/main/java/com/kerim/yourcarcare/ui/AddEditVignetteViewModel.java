package com.kerim.yourcarcare.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.data.VignetteRepository;
import com.kerim.yourcarcare.model.Vignette;
import com.kerim.yourcarcare.model.VignetteType;

import java.time.LocalDate;

/** Data for AddEditVignetteActivity. */
public class AddEditVignetteViewModel extends AndroidViewModel {

    private final VignetteRepository repository;
    private LiveData<Vignette> vignette;

    // These live here (not in the activity) so they survive screen rotation
    boolean formFilled = false;
    VignetteType selectedType = VignetteType.YEARLY;
    LocalDate selectedDate = LocalDate.now();

    public AddEditVignetteViewModel(@NonNull Application application) {
        super(application);
        repository = new VignetteRepository(application);
    }

    public LiveData<Vignette> getVignette(long id) {
        if (vignette == null) {
            vignette = repository.getById(id);
        }
        return vignette;
    }

    public void save(Vignette vignette) {
        repository.save(vignette);
    }
}
