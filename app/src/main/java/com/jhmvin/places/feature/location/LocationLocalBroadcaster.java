package com.jhmvin.places.feature.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class LocationLocalBroadcaster implements LocationBroadcaster {

    private Context context;

    public LocationLocalBroadcaster(Context context) {
        this.context = context;
    }

    @Override
    public void broadcastLocation(Location location) {
        // create location intent.
        Intent locationIntent = new Intent(LocationBroadcaster.LOCATION_LOCAL_BROADCAST);
        Bundle locationBundle = new Bundle();
        locationBundle.putParcelable(LocationBroadcaster.LOCATION_PARCEL, location);
        locationIntent.putExtra(LocationBroadcaster.LOCATION_BUNDLE, locationBundle);
        // broadcast intent.
        LocalBroadcastManager.getInstance(this.context.getApplicationContext())
                .sendBroadcast(locationIntent);
    }
}
