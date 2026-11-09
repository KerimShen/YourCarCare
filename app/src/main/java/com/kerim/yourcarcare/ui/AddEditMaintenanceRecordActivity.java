package com.kerim.yourcarcare.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.MaintenanceType;

import java.time.LocalDate;

/** One screen for both adding and editing a maintenance record. */
public class AddEditMaintenanceRecordActivity extends AppCompatActivity {

    /** Id of the car the record belongs to. Required. */
    public static final String EXTRA_CAR_ID = "com.kerim.yourcarcare.EXTRA_CAR_ID";
    /** Id of the record to edit. Missing = add a new record. */
    public static final String EXTRA_RECORD_ID = "com.kerim.yourcarcare.EXTRA_RECORD_ID";

    private static final String DATE_PICKER_TAG = "date_picker";

    private AddEditMaintenanceRecordViewModel viewModel;
    private long carId;
    private long recordId; // 0 = new record

    private TextInputLayout layoutDate, layoutKm;
    private TextInputEditText editDate, editKm, editNote;
    private MaterialAutoCompleteTextView dropdownType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_edit_maintenance_record);
        SystemBars.applyPadding(findViewById(R.id.root));

        carId = getIntent().getLongExtra(EXTRA_CAR_ID, 0);
        recordId = getIntent().getLongExtra(EXTRA_RECORD_ID, 0);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(recordId == 0 ? R.string.title_add_record : R.string.title_edit_record);

        layoutDate = findViewById(R.id.layout_date);
        layoutKm = findViewById(R.id.layout_km);
        editDate = findViewById(R.id.edit_date);
        editKm = findViewById(R.id.edit_km);
        editNote = findViewById(R.id.edit_note);
        dropdownType = findViewById(R.id.dropdown_maintenance_type);

        EnumDropdown.setup(dropdownType, MaintenanceType.values());

        viewModel = new ViewModelProvider(this).get(AddEditMaintenanceRecordViewModel.class);

        if (!viewModel.formFilled) {
            if (recordId == 0) {
                // New record: oil change, today, and the car's current km (once the car loads)
                EnumDropdown.select(dropdownType, MaintenanceType.OIL_CHANGE);
                viewModel.getCar(carId).observe(this, car -> {
                    if (car != null && !viewModel.formFilled) {
                        editKm.setText(String.valueOf(car.currentKm));
                        viewModel.formFilled = true;
                    }
                });
            } else {
                viewModel.getRecord(recordId).observe(this, record -> {
                    if (record != null && !viewModel.formFilled) {
                        fillForm(record);
                        viewModel.formFilled = true;
                    }
                });
            }
        }
        showDate();

        editDate.setOnClickListener(v -> openDatePicker());
        reattachDatePicker();

        findViewById(R.id.button_save).setOnClickListener(v -> saveRecord());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fillForm(MaintenanceRecord record) {
        EnumDropdown.select(dropdownType, record.maintenanceType);
        viewModel.selectedDate = record.date;
        showDate();
        editKm.setText(String.valueOf(record.km));
        editNote.setText(record.note);
    }

    private void showDate() {
        editDate.setText(Dates.format(viewModel.selectedDate));
    }

    private void openDatePicker() {
        // Grey out future days in the calendar
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.hint_date)
                .setSelection(Dates.toUtcMillis(viewModel.selectedDate))
                .setCalendarConstraints(constraints)
                .build();
        addDateListener(picker);
        picker.show(getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    /**
     * The picker is a dialog fragment: Android re-creates it after a rotation,
     * but our listener is lost. Find it by its tag and attach the listener again.
     */
    @SuppressWarnings("unchecked")
    private void reattachDatePicker() {
        MaterialDatePicker<Long> picker = (MaterialDatePicker<Long>)
                getSupportFragmentManager().findFragmentByTag(DATE_PICKER_TAG);
        if (picker != null) {
            addDateListener(picker);
        }
    }

    private void addDateListener(MaterialDatePicker<Long> picker) {
        picker.addOnPositiveButtonClickListener(selection -> {
            viewModel.selectedDate = Dates.fromUtcMillis(selection);
            showDate();
        });
    }

    private void saveRecord() {
        if (!validate()) {
            return;
        }
        MaintenanceRecord record = new MaintenanceRecord();
        record.id = recordId; // 0 -> insert, otherwise update
        record.carId = carId;
        record.maintenanceType = EnumDropdown.getSelected(dropdownType, MaintenanceType.values());
        record.date = viewModel.selectedDate;
        record.km = Integer.parseInt(text(editKm));
        String note = text(editNote);
        record.note = note.isEmpty() ? null : note;

        viewModel.save(record);
        finish();
    }

    /** Checks the fields, shows errors and returns true if everything is OK. */
    private boolean validate() {
        layoutDate.setError(null);
        layoutKm.setError(null);
        boolean valid = true;

        if (viewModel.selectedDate.isAfter(LocalDate.now())) {
            layoutDate.setError(getString(R.string.error_date_future));
            valid = false;
        }

        String kmText = text(editKm);
        if (kmText.isEmpty()) {
            layoutKm.setError(getString(R.string.error_required));
            valid = false;
        } else {
            try {
                if (Integer.parseInt(kmText) < 0) {
                    layoutKm.setError(getString(R.string.error_km_negative));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                layoutKm.setError(getString(R.string.error_invalid_number));
                valid = false;
            }
        }
        return valid;
    }

    private static String text(TextInputEditText field) {
        return field.getText() == null ? "" : field.getText().toString().trim();
    }
}
