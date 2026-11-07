package com.kerim.yourcarcare.model;

import androidx.annotation.StringRes;

/**
 * Implemented by enums that have a user-visible name.
 * The name lives in strings.xml so it can be translated (English / Bulgarian).
 */
public interface HasLabel {

    // A string resource id like R.string.engine_type_petrol
    @StringRes
    int getLabelRes();
}
