package com.kerim.yourcarcare.model;

import com.kerim.yourcarcare.R;

/** Type of engine (fuel) the car uses. */
public enum EngineType implements HasLabel {
    PETROL(R.string.engine_type_petrol),
    DIESEL(R.string.engine_type_diesel),
    HYBRID(R.string.engine_type_hybrid),
    ELECTRIC(R.string.engine_type_electric);

    // Room still saves the constant's name (e.g. "PETROL"), not this label
    private final int labelRes;

    EngineType(int labelRes) {
        this.labelRes = labelRes;
    }

    @Override
    public int getLabelRes() {
        return labelRes;
    }
}
