package com.kerim.yourcarcare.ui;

import android.os.Bundle;
import android.widget.TextView;

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
import com.kerim.yourcarcare.logic.VignetteRules;
import com.kerim.yourcarcare.model.Vignette;
import com.kerim.yourcarcare.model.VignetteType;

import java.time.LocalDate;

/** One screen for both adding and editing a vignette. */
public class AddEditVignetteActivity extends AppCompatActivity {

    /** Id of the car the vignette belongs to. Required. */
    public static final String EXTRA_CAR_ID = "com.kerim.yourcarcare.EXTRA_CAR_ID";
    /** Id of the vignette to edit. Missing = add a new vignette. */
    public static final String EXTRA_VIGNETTE_ID = "com.kerim.yourcarcare.EXTRA_VIGNETTE_ID";

    private static final String DATE_PICKER_TAG = "date_picker";

    private AddEditVignetteViewModel viewModel;
    private long carId;
    private long vignetteId; // 0 = new vignette

    private MaterialAutoCompleteTextView dropdownType;
    private TextInputLayout layoutStartDate;
    private TextInputEditText editStartDate;
    private TextView textExpiryPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_edit_vignette);
        SystemBars.applyPadding(findViewById(R.id.root));

        carId = getIntent().getLongExtra(EXTRA_CAR_ID, 0);
        vignetteId = getIntent().getLongExtra(EXTRA_VIGNETTE_ID, 0);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(vignetteId == 0 ? R.string.title_add_vignette : R.string.title_edit_vignette);

        dropdownType = findViewById(R.id.dropdown_vignette_type);
        layoutStartDate = findViewById(R.id.layout_start_date);
        editStartDate = findViewById(R.id.edit_start_date);
        textExpiryPreview = findViewById(R.id.text_expiry_preview);

        EnumDropdown.setup(dropdownType, VignetteType.values());
        // When the user picks a type, remember it and update the expiry preview
        dropdownType.setOnItemClickListener((parent, view, position, id) -> {
            VignetteType type = EnumDropdown.getSelected(dropdownType, VignetteType.values());
            if (type != null) {
                viewModel.selectedType = type;
                updateDateAndPreview();
            }
        });

        viewModel = new ViewModelProvider(this).get(AddEditVignetteViewModel.class);

        if (!viewModel.formFilled) {
            if (vignetteId == 0) {
                // New vignette: defaults are already in the ViewModel (yearly, today)
                EnumDropdown.select(dropdownType, viewModel.selectedType);
                viewModel.formFilled = true;
            } else {
                viewModel.getVignette(vignetteId).observe(this, vignette -> {
                    if (vignette != null && !viewModel.formFilled) {
                        viewModel.selectedType = vignette.type;
                        viewModel.selectedDate = vignette.startDate;
                        EnumDropdown.select(dropdownType, vignette.type);
                        updateDateAndPreview();
                        viewModel.formFilled = true;
                    }
                });
            }
        }
        updateDateAndPreview();

        editStartDate.setOnClickListener(v -> openDatePicker());
        reattachDatePicker();

        findViewById(R.id.button_save).setOnClickListener(v -> saveVignette());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /** Shows the selected start date and the expiry date calculated from it. */
    private void updateDateAndPreview() {
        editStartDate.setText(Dates.format(viewModel.selectedDate));

        // A temporary Vignette, only used to reuse getExpiryDate()
        Vignette preview = new Vignette();
        preview.type = viewModel.selectedType;
        preview.startDate = viewModel.selectedDate;
        textExpiryPreview.setText(getString(R.string.vignette_valid_until,
                Dates.format(preview.getExpiryDate())));
    }

    private void openDatePicker() {
        // Past days are allowed; days more than MAX_DAYS_AHEAD in the future are greyed out
        LocalDate lastAllowed = LocalDate.now().plusDays(VignetteRules.MAX_DAYS_AHEAD);
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.before(Dates.toUtcMillis(lastAllowed)))
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.hint_start_date)
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
            updateDateAndPreview();
        });
    }

    private void saveVignette() {
        if (!validate()) {
            return;
        }
        Vignette vignette = new Vignette();
        vignette.id = vignetteId; // 0 -> insert, otherwise update
        vignette.carId = carId;
        vignette.type = viewModel.selectedType;
        vignette.startDate = viewModel.selectedDate;

        viewModel.save(vignette);
        finish();
    }

    /** Checks the start date and shows an error on the field if it's wrong. */
    private boolean validate() {
        layoutStartDate.setError(null);
        LocalDate start = viewModel.selectedDate;

        if (VignetteRules.isStartTooFarAhead(start, LocalDate.now())) {
            layoutStartDate.setError(getString(R.string.error_start_too_far, VignetteRules.MAX_DAYS_AHEAD));
            return false;
        }
        if (!VignetteRules.isValidStartDay(viewModel.selectedType, start)) {
            layoutStartDate.setError(getString(R.string.error_weekend_start));
            return false;
        }
        return true;
    }
}
