package com.jhmvin.places.feature.location;

import android.location.Location;

public interface LocationBroadcaster {

    public final static String LOCATION_LOCAL_BROADCAST = LocationBroadcaster.class
            .getCanonicalName() + ".LOCATION_LOCAL_BROADCAST";

    public final static String LOCATION_BUNDLE = "LOCATION_BUNDLE";
    public final static String LOCATION_PARCEL = "LOCATION_PARCEL";

    void broadcastLocation(Location location);

}
