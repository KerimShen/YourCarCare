package com.kerim.yourcarcare.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.DrivingStyle;
import com.kerim.yourcarcare.model.EngineType;
import com.kerim.yourcarcare.model.Transmission;

import java.time.LocalDate;

/** One screen for both adding a new car and editing an existing one. */
public class AddEditCarActivity extends AppCompatActivity {

    /** Intent extra with the id of the car to edit. Missing = add a new car. */
    public static final String EXTRA_CAR_ID = "com.kerim.yourcarcare.EXTRA_CAR_ID";

    private static final int MIN_YEAR = 1950;

    private AddEditCarViewModel viewModel;
    private long carId; // 0 = new car

    private TextInputLayout layoutBrand, layoutModel, layoutYear, layoutCurrentKm;
    private TextInputEditText editBrand, editModel, editYear, editCurrentKm;
    private MaterialAutoCompleteTextView dropdownEngineType, dropdownTransmission, dropdownDrivingStyle;
    private MaterialSwitch switchTurbo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_edit_car);
        SystemBars.applyPadding(findViewById(R.id.root));

        carId = getIntent().getLongExtra(EXTRA_CAR_ID, 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back arrow
        setTitle(carId == 0 ? R.string.title_add_car : R.string.title_edit_car);

        findViews();
        EnumDropdown.setup(dropdownEngineType, EngineType.values());
        EnumDropdown.setup(dropdownTransmission, Transmission.values());
        EnumDropdown.setup(dropdownDrivingStyle, DrivingStyle.values());

        viewModel = new ViewModelProvider(this).get(AddEditCarViewModel.class);

        if (!viewModel.formFilled) {
            if (carId == 0) {
                fillDefaults();
                viewModel.formFilled = true;
            } else {
                // The car loads in the background; fill the form when it arrives
                viewModel.getCar(carId).observe(this, car -> {
                    if (car != null && !viewModel.formFilled) {
                        fillForm(car);
                        viewModel.formFilled = true;
                    }
                });
            }
        }

        findViewById(R.id.button_save).setOnClickListener(v -> saveCar());
    }

    // Back arrow in the toolbar: just close this screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void findViews() {
        layoutBrand = findViewById(R.id.layout_brand);
        layoutModel = findViewById(R.id.layout_model);
        layoutYear = findViewById(R.id.layout_year);
        layoutCurrentKm = findViewById(R.id.layout_current_km);
        editBrand = findViewById(R.id.edit_brand);
        editModel = findViewById(R.id.edit_model);
        editYear = findViewById(R.id.edit_year);
        editCurrentKm = findViewById(R.id.edit_current_km);
        dropdownEngineType = findViewById(R.id.dropdown_engine_type);
        dropdownTransmission = findViewById(R.id.dropdown_transmission);
        dropdownDrivingStyle = findViewById(R.id.dropdown_driving_style);
        switchTurbo = findViewById(R.id.switch_turbo);
    }

    private void fillDefaults() {
        EnumDropdown.select(dropdownEngineType, EngineType.PETROL);
        EnumDropdown.select(dropdownTransmission, Transmission.MANUAL);
        EnumDropdown.select(dropdownDrivingStyle, DrivingStyle.GENTLE);
    }

    private void fillForm(Car car) {
        editBrand.setText(car.brand);
        editModel.setText(car.model);
        editYear.setText(String.valueOf(car.year));
        editCurrentKm.setText(String.valueOf(car.currentKm));
        switchTurbo.setChecked(car.isTurbo);
        EnumDropdown.select(dropdownEngineType, car.engineType);
        EnumDropdown.select(dropdownTransmission, car.transmission);
        EnumDropdown.select(dropdownDrivingStyle, car.drivingStyle);
    }

    private void saveCar() {
        if (!validate()) {
            return;
        }
        Car car = new Car();
        car.id = carId; // 0 -> insert, otherwise update the existing row
        car.brand = text(editBrand);
        car.model = text(editModel);
        car.year = Integer.parseInt(text(editYear));
        car.currentKm = Integer.parseInt(text(editCurrentKm));
        car.isTurbo = switchTurbo.isChecked();
        car.engineType = EnumDropdown.getSelected(dropdownEngineType, EngineType.values());
        car.transmission = EnumDropdown.getSelected(dropdownTransmission, Transmission.values());
        car.drivingStyle = EnumDropdown.getSelected(dropdownDrivingStyle, DrivingStyle.values());

        viewModel.save(car);
        // The list screen updates by itself through LiveData
        finish();
    }

    /** Checks every field, shows errors on the wrong ones and returns true if all are OK. */
    private boolean validate() {
        // Clear old errors first, so fixed fields don't keep showing them
        layoutBrand.setError(null);
        layoutModel.setError(null);
        layoutYear.setError(null);
        layoutCurrentKm.setError(null);

        boolean valid = true;

        if (text(editBrand).isEmpty()) {
            layoutBrand.setError(getString(R.string.error_required));
            valid = false;
        }
        if (text(editModel).isEmpty()) {
            layoutModel.setError(getString(R.string.error_required));
            valid = false;
        }

        int maxYear = LocalDate.now().getYear() + 1;
        String yearText = text(editYear);
        if (yearText.isEmpty()) {
            layoutYear.setError(getString(R.string.error_required));
            valid = false;
        } else {
            try {
                int year = Integer.parseInt(yearText);
                if (year < MIN_YEAR || year > maxYear) {
                    layoutYear.setError(getString(R.string.error_year_range, MIN_YEAR, maxYear));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                // e.g. a number too big to fit in an int
                layoutYear.setError(getString(R.string.error_year_range, MIN_YEAR, maxYear));
                valid = false;
            }
        }

        String kmText = text(editCurrentKm);
        if (kmText.isEmpty()) {
            layoutCurrentKm.setError(getString(R.string.error_required));
            valid = false;
        } else {
            try {
                if (Integer.parseInt(kmText) < 0) {
                    layoutCurrentKm.setError(getString(R.string.error_km_negative));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                layoutCurrentKm.setError(getString(R.string.error_invalid_number));
                valid = false;
            }
        }

        return valid;
    }

    /** Text of a field without spaces at the start and end. */
    private static String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }
}
