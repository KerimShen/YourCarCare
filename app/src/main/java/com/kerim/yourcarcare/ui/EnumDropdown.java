package com.kerim.yourcarcare.ui;

import android.content.Context;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.kerim.yourcarcare.model.HasLabel;

/**
 * Helpers for Material "exposed dropdown menus" that show enum values.
 * The dropdown only stores text, so we turn enum -> label when showing
 * and label -> enum when reading. <T extends HasLabel> means "any enum with a label".
 */
public final class EnumDropdown {

    private EnumDropdown() {
        // Only static methods, no objects needed
    }

    /** Fills the dropdown with the translated labels of all values. */
    public static <T extends HasLabel> void setup(MaterialAutoCompleteTextView view, T[] values) {
        Context context = view.getContext();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = context.getString(values[i].getLabelRes());
        }
        view.setSimpleItems(labels);
    }

    /** Shows the given value as the current selection. */
    public static void select(MaterialAutoCompleteTextView view, HasLabel value) {
        String label = view.getContext().getString(value.getLabelRes());
        // false = don't filter the list by this text, so all options stay visible
        view.setText(label, false);
    }

    /** Returns the value whose label is shown, or null if nothing matches. */
    public static <T extends HasLabel> T getSelected(MaterialAutoCompleteTextView view, T[] values) {
        String text = view.getText().toString();
        for (T value : values) {
            if (view.getContext().getString(value.getLabelRes()).equals(text)) {
                return value;
            }
        }
        return null;
    }
}
