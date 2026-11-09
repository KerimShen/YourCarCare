package com.kerim.yourcarcare.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.kerim.yourcarcare.model.Vignette;

import java.util.List;

/** Vignette data for the UI. Writes run on a background thread. */
public class VignetteRepository {

    private final VignetteDao vignetteDao;

    public VignetteRepository(Application application) {
        vignetteDao = AppDatabase.getInstance(application).vignetteDao();
    }

    public LiveData<List<Vignette>> getForCar(long carId) {
        return vignetteDao.getForCar(carId);
    }

    public void insert(Vignette vignette) {
        AppDatabase.databaseExecutor.execute(() -> vignetteDao.insert(vignette));
    }

    public void update(Vignette vignette) {
        AppDatabase.databaseExecutor.execute(() -> vignetteDao.update(vignette));
    }

    public void delete(Vignette vignette) {
        AppDatabase.databaseExecutor.execute(() -> vignetteDao.delete(vignette));
    }
}
