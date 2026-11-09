package com.kerim.yourcarcare.ui;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.kerim.yourcarcare.R;
import com.kerim.yourcarcare.model.Car;
import com.kerim.yourcarcare.model.MaintenanceRecord;
import com.kerim.yourcarcare.model.Vignette;

/** "Are you sure?" dialogs shared by several screens. onConfirm runs only if the user taps Delete. */
public final class ConfirmDialogs {

    private ConfirmDialogs() {
    }

    public static void deleteCar(Context context, Car car, Runnable onConfirm) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_car_title)
                .setMessage(context.getString(R.string.delete_car_message, car.brand, car.model))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> onConfirm.run())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    public static void deleteRecord(Context context, MaintenanceRecord record, Runnable onConfirm) {
        String type = context.getString(record.maintenanceType.getLabelRes());
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_record_title)
                .setMessage(context.getString(R.string.delete_record_message, type, Dates.format(record.date)))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> onConfirm.run())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    public static void deleteVignette(Context context, Vignette vignette, Runnable onConfirm) {
        String type = context.getString(vignette.type.getLabelRes());
        String period = context.getString(R.string.vignette_period,
                Dates.format(vignette.startDate), Dates.format(vignette.getExpiryDate()));
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.delete_vignette_title)
                .setMessage(context.getString(R.string.delete_vignette_message, type, period))
                .setPositiveButton(R.string.action_delete, (dialog, which) -> onConfirm.run())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
