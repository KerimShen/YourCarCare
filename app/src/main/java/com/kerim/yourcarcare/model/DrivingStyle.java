package com.kerim.yourcarcare.model;

import com.kerim.yourcarcare.R;

/** How hard the owner drives. Later used to shorten or extend maintenance intervals. */
public enum DrivingStyle implements HasLabel {
    VERY_GENTLE(R.string.driving_style_very_gentle),
    GENTLE(R.string.driving_style_gentle),
    OCCASIONALLY_HARD(R.string.driving_style_occasionally_hard),
    HARD(R.string.driving_style_hard);

    // Room still saves the constant's name (e.g. "VERY_GENTLE"), not this label
    private final int labelRes;

    DrivingStyle(int labelRes) {
        this.labelRes = labelRes;
    }

    @Override
    public int getLabelRes() {
        return labelRes;
    }
}
