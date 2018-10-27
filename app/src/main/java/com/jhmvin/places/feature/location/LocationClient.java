package com.jhmvin.places.feature.location;

import android.location.Location;

/**
 * Interface for location services.
 */
public interface LocationClient {

    /**
     * starts location service. inquires location.
     */
    void startLocationAwareness();

    /**
     * What to do on location obtained.
     *
     * @param callback
     */
    void setLocationCallback(OnLocationObtained callback);

    /**
     * Stops the location service.
     */
    void stopLocationAwareness();

    /**
     * Checks whether the service is running.
     *
     * @return
     */
    boolean isLocationAware();


    /**
     * Call back interface.
     */
    @FunctionalInterface
    public interface OnLocationObtained {
        void onLocationObtained(Location location);
    }

}
