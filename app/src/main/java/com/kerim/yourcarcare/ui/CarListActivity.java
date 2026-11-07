package com.kerim.yourcarcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Car;

/** Launcher screen: the list of the user's cars. */
public class CarListActivity extends AppCompatActivity implements CarAdapter.OnCarClickListener {

    private CarListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_car_list);
        SystemBars.applyPadding(findViewById(R.id.root));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView carList = findViewById(R.id.car_list);
        TextView emptyText = findViewById(R.id.empty_text);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        CarAdapter adapter = new CarAdapter(this);
        carList.setAdapter(adapter);

        // ViewModelProvider returns the same ViewModel after a rotation instead of a new one
        viewModel = new ViewModelProvider(this).get(CarListViewModel.class);

        // Called now and again whenever the cars table changes (add, edit, delete)
        viewModel.getCars().observe(this, cars -> {
            adapter.submitList(cars);
            emptyText.setVisibility(cars.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // No car id in the intent = add a new car
        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddEditCarActivity.class)));
    }

    @Override
    public void onCarClick(Car car) {
        Intent intent = new Intent(this, AddEditCarActivity.class);
        intent.putExtra(AddEditCarActivity.EXTRA_CAR_ID, car.id);
        startActivity(intent);
    }

    @Override
    public void onCarLongClick(Car car) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_car_title)
                .setMessage(getString(R.string.delete_car_message, car.brand, car.model))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> viewModel.delete(car))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
