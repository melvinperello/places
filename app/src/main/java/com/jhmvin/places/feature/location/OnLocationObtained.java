package com.jhmvin.places.feature.location;

import android.location.Location;

@FunctionalInterface
public interface OnLocationObtained {
    void onLocationObtained(Location location);
}
