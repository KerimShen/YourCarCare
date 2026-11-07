package com.kerim.yourcarcare.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Car;

/**
 * Shows cars in the RecyclerView.
 * ListAdapter compares the old and new list (using DIFF_CALLBACK below) and only
 * redraws the rows that changed, instead of refreshing the whole list.
 */
public class CarAdapter extends ListAdapter<Car, CarAdapter.CarViewHolder> {

    /** Lets the activity react to taps without the adapter knowing about the activity. */
    public interface OnCarClickListener {
        void onCarClick(Car car);

        void onCarLongClick(Car car);
    }

    private final OnCarClickListener listener;

    public CarAdapter(OnCarClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = getItem(position);
        holder.title.setText(holder.itemView.getContext()
                .getString(R.string.car_item_title, car.brand, car.model));
        holder.details.setText(holder.itemView.getContext()
                .getString(R.string.car_item_details, car.year, car.currentKm));

        holder.itemView.setOnClickListener(v -> listener.onCarClick(car));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onCarLongClick(car);
            return true; // true = we handled it, so a normal click doesn't fire too
        });
    }

    /** Holds the views of one row so we don't call findViewById every time. */
    static class CarViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView details;

        CarViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.car_title);
            details = itemView.findViewById(R.id.car_details);
        }
    }

    private static final DiffUtil.ItemCallback<Car> DIFF_CALLBACK = new DiffUtil.ItemCallback<Car>() {
        // Same car? (same database row)
        @Override
        public boolean areItemsTheSame(@NonNull Car oldCar, @NonNull Car newCar) {
            return oldCar.id == newCar.id;
        }

        // Same car, but did anything shown in the row change?
        @Override
        public boolean areContentsTheSame(@NonNull Car oldCar, @NonNull Car newCar) {
            return oldCar.brand.equals(newCar.brand)
                    && oldCar.model.equals(newCar.model)
                    && oldCar.year == newCar.year
                    && oldCar.currentKm == newCar.currentKm;
        }
    };
}
