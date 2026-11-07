package com.kerim.yourcarcare.model;

import com.kerim.yourcarcare.R;

/** Type of gearbox. */
public enum Transmission implements HasLabel {
    MANUAL(R.string.transmission_manual),
    AUTOMATIC(R.string.transmission_automatic);

    // Room still saves the constant's name (e.g. "MANUAL"), not this label
    private final int labelRes;

    Transmission(int labelRes) {
        this.labelRes = labelRes;
    }

    @Override
    public int getLabelRes() {
        return labelRes;
    }
}
