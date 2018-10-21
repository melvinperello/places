package com.jhmvin.places.feature.location;

public interface LocationClient {

    void startLocationAwareness();

    void setLocationCallback(OnLocationObtained callback);

    void stopLocationAwareness();

    boolean isLocationAware();
}
