package com.kerim.yourcarcare.model;

import com.kerim.yourcarcare.R;

/** Duration options for the Bulgarian e-vignette. */
public enum VignetteType implements HasLabel {
    DAILY(R.string.vignette_type_daily),
    WEEKEND(R.string.vignette_type_weekend),
    WEEKLY(R.string.vignette_type_weekly),
    MONTHLY(R.string.vignette_type_monthly),
    QUARTERLY(R.string.vignette_type_quarterly),
    YEARLY(R.string.vignette_type_yearly);

    // Room still saves the constant's name (e.g. "DAILY"), not this label
    private final int labelRes;

    VignetteType(int labelRes) {
        this.labelRes = labelRes;
    }

    @Override
    public int getLabelRes() {
        return labelRes;
    }
}
