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
import com.kerim.yourcarcare.model.MaintenanceRecord;

import java.util.Objects;

/** Shows a car's maintenance records. Works the same way as CarAdapter. */
public class MaintenanceRecordAdapter
        extends ListAdapter<MaintenanceRecord, MaintenanceRecordAdapter.RecordViewHolder> {

    public interface OnRecordClickListener {
        void onRecordClick(MaintenanceRecord record);

        void onRecordLongClick(MaintenanceRecord record);
    }

    private final OnRecordClickListener listener;

    public MaintenanceRecordAdapter(OnRecordClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maintenance_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        MaintenanceRecord record = getItem(position);
        Context context = holder.itemView.getContext();

        holder.type.setText(record.maintenanceType.getLabelRes());
        holder.details.setText(context.getString(
                R.string.record_item_details, Dates.format(record.date), record.km));

        boolean hasNote = record.note != null && !record.note.isEmpty();
        holder.note.setVisibility(hasNote ? View.VISIBLE : View.GONE);
        holder.note.setText(record.note);

        holder.itemView.setOnClickListener(v -> listener.onRecordClick(record));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onRecordLongClick(record);
            return true;
        });
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        final TextView type;
        final TextView details;
        final TextView note;

        RecordViewHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.record_type);
            details = itemView.findViewById(R.id.record_details);
            note = itemView.findViewById(R.id.record_note);
        }
    }

    private static final DiffUtil.ItemCallback<MaintenanceRecord> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MaintenanceRecord>() {
                @Override
                public boolean areItemsTheSame(@NonNull MaintenanceRecord oldItem,
                                               @NonNull MaintenanceRecord newItem) {
                    return oldItem.id == newItem.id;
                }

                // Objects.equals also works when one of the values is null (e.g. note)
                @Override
                public boolean areContentsTheSame(@NonNull MaintenanceRecord oldItem,
                                                  @NonNull MaintenanceRecord newItem) {
                    return Objects.equals(oldItem.maintenanceType, newItem.maintenanceType)
                            && Objects.equals(oldItem.date, newItem.date)
                            && oldItem.km == newItem.km
                            && Objects.equals(oldItem.note, newItem.note);
                }
            };
}
