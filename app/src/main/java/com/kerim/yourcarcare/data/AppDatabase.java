package com.kerim.yourcarcare.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.Vignette;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The app's Room database. Lists every table (entity) and gives access to the DAOs.
 * Increase "version" whenever you change an entity's fields.
 */
@Database(
        entities = {Car.class, Vignette.class, MaintenanceRecord.class},
        version = 1,
        // Room can save a JSON copy of the schema for migrations; not needed yet
        exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    // Room generates the code behind these methods
    public abstract CarDao carDao();

    public abstract VignetteDao vignetteDao();

    public abstract MaintenanceRecordDao maintenanceRecordDao();

    // Background threads for writes (insert/update/delete). Android doesn't allow
    // database work on the main thread because it would freeze the screen.
    // All repositories share this one pool of 2 threads.
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(2);

    // Opening a database is expensive, so the whole app shares one instance (singleton).
    // "volatile" makes sure every thread sees the latest value of INSTANCE.
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            // synchronized: if two threads get here at the same time, only one creates the database
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "yourcarcare.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
