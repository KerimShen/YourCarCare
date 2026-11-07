package com.kerim.yourcarcare.model;

import com.kerim.yourcarcare.R;

/** Kinds of service work that can be recorded for a car. */
public enum MaintenanceType implements HasLabel {
    OIL_CHANGE(R.string.maintenance_type_oil_change),
    AIR_FILTER(R.string.maintenance_type_air_filter),
    CABIN_FILTER(R.string.maintenance_type_cabin_filter),
    SPARK_PLUGS(R.string.maintenance_type_spark_plugs),
    BRAKE_PADS(R.string.maintenance_type_brake_pads),
    BRAKE_FLUID(R.string.maintenance_type_brake_fluid),
    COOLANT(R.string.maintenance_type_coolant),
    TIMING_BELT(R.string.maintenance_type_timing_belt),
    GEARBOX_OIL(R.string.maintenance_type_gearbox_oil);

    // Room still saves the constant's name (e.g. "OIL_CHANGE"), not this label
    private final int labelRes;

    MaintenanceType(int labelRes) {
        this.labelRes = labelRes;
    }

    @Override
    public int getLabelRes() {
        return labelRes;
    }
}
