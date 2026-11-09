package com.kerim.yourcarcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;

/** Shows one car: a header with its data, then its maintenance history. */
public class CarDetailActivity extends AppCompatActivity
        implements MaintenanceRecordAdapter.OnRecordClickListener {

    /** Intent extra with the id of the car to show. Required. */
    public static final String EXTRA_CAR_ID = "com.kerim.yourcarcare.EXTRA_CAR_ID";

    private CarDetailViewModel viewModel;
    private long carId;
    private Car car; // latest loaded car, used by the menu

    private TextView textCarTitle, textYear, textEngine, textTransmission, textKm, textDrivingStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_car_detail);
        SystemBars.applyPadding(findViewById(R.id.root));

        carId = getIntent().getLongExtra(EXTRA_CAR_ID, 0);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textCarTitle = findViewById(R.id.text_car_title);
        textYear = findViewById(R.id.text_year);
        textEngine = findViewById(R.id.text_engine);
        textTransmission = findViewById(R.id.text_transmission);
        textKm = findViewById(R.id.text_km);
        textDrivingStyle = findViewById(R.id.text_driving_style);

        RecyclerView recordList = findViewById(R.id.record_list);
        TextView emptyRecordsText = findViewById(R.id.empty_records_text);
        MaintenanceRecordAdapter adapter = new MaintenanceRecordAdapter(this);
        recordList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(CarDetailViewModel.class);
        viewModel.init(carId);

        viewModel.getCar().observe(this, loadedCar -> {
            if (loadedCar == null) {
                // The car was deleted, nothing left to show
                finish();
                return;
            }
            car = loadedCar;
            showCar(loadedCar);
        });

        viewModel.getRecords().observe(this, records -> {
            adapter.submitList(records);
            emptyRecordsText.setVisibility(records.isEmpty() ? View.VISIBLE : View.GONE);
        });

        findViewById(R.id.fab_add_record).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditMaintenanceRecordActivity.class);
            intent.putExtra(AddEditMaintenanceRecordActivity.EXTRA_CAR_ID, carId);
            startActivity(intent);
        });
    }

    private void showCar(Car car) {
        String title = getString(R.string.car_item_title, car.brand, car.model);
        setTitle(title);
        textCarTitle.setText(title);
        textYear.setText(getString(R.string.detail_year, car.year));

        String engine = getString(car.engineType.getLabelRes());
        if (car.isTurbo) {
            engine = getString(R.string.engine_with_turbo, engine);
        }
        textEngine.setText(getString(R.string.detail_engine, engine));

        textTransmission.setText(getString(R.string.detail_transmission,
                getString(car.transmission.getLabelRes())));
        textKm.setText(getString(R.string.detail_km, car.currentKm));
        textDrivingStyle.setText(getString(R.string.detail_driving_style,
                getString(car.drivingStyle.getLabelRes())));
    }

    // Puts the Edit / Delete items into the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditCarActivity.class);
            intent.putExtra(AddEditCarActivity.EXTRA_CAR_ID, carId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            if (car != null) {
                ConfirmDialogs.deleteCar(this, car, () -> {
                    viewModel.deleteCar(car);
                    finish();
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRecordClick(MaintenanceRecord record) {
        Intent intent = new Intent(this, AddEditMaintenanceRecordActivity.class);
        intent.putExtra(AddEditMaintenanceRecordActivity.EXTRA_CAR_ID, carId);
        intent.putExtra(AddEditMaintenanceRecordActivity.EXTRA_RECORD_ID, record.id);
        startActivity(intent);
    }

    @Override
    public void onRecordLongClick(MaintenanceRecord record) {
        ConfirmDialogs.deleteRecord(this, record, () -> viewModel.deleteRecord(record));
    }
}
