package com.kerim.yourcarcare.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Vignette;

import java.util.Objects;

/** Rows of the vignette history inside the vignette card. Same pattern as MaintenanceRecordAdapter. */
public class VignetteAdapter extends ListAdapter<Vignette, VignetteAdapter.VignetteViewHolder> {

    public interface OnVignetteClickListener {
        void onVignetteClick(Vignette vignette);

        void onVignetteLongClick(Vignette vignette);
    }

    private final OnVignetteClickListener listener;

    public VignetteAdapter(OnVignetteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VignetteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vignette, parent, false);
        return new VignetteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VignetteViewHolder holder, int position) {
        Vignette vignette = getItem(position);
        Context context = holder.itemView.getContext();

        holder.type.setText(vignette.type.getLabelRes());
        holder.period.setText(context.getString(R.string.vignette_period,
                Dates.format(vignette.startDate), Dates.format(vignette.getExpiryDate())));

        holder.itemView.setOnClickListener(v -> listener.onVignetteClick(vignette));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onVignetteLongClick(vignette);
            return true;
        });
    }

    static class VignetteViewHolder extends RecyclerView.ViewHolder {
        final TextView type;
        final TextView period;

        VignetteViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.vignette_item_type);
            period = itemView.findViewById(R.id.vignette_item_period);
        }
    }

    private static final DiffUtil.ItemCallback<Vignette> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Vignette>() {
                @Override
                public boolean areItemsTheSame(@NonNull Vignette oldItem, @NonNull Vignette newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Vignette oldItem, @NonNull Vignette newItem) {
                    return Objects.equals(oldItem.type, newItem.type)
                            && Objects.equals(oldItem.startDate, newItem.startDate);
                }
            };
}
