package com.jhmvin.places.feature.location;

import android.content.Context;
import android.location.Location;

import com.jhmvin.places.feature.Serviceable;

/**
 * Holds the Location Inquirer and Location Broadcaster together to deliver location service
 * to the entire application by broadcasting it locally in a bundle.
 */
public class LocationService implements Serviceable {

    private Context context;
    //
    // Dependency Inversion
    //  depend of abstraction.
    //
    // interface
    private LocationBroadcaster locationBroadcaster;
    // abstract class
    private AbstractLocationInquirer locationInquirer;

    public LocationService(Context context) {
        this.context = context;
    }

    @Override
    public void start() {
        this.locationInquirer = new FusedLocationInquirer(this.context);
        this.locationBroadcaster = new LocationLocalBroadcaster(this.context);

        this.locationInquirer.setOnLocationUpdated(new AbstractLocationInquirer.OnLocationUpdated() {
            @Override
            public void onLocationUpdated(Location location) {
                locationBroadcaster.broadcastLocation(location);
            }
        });

        this.locationInquirer.start();
    }

    @Override
    public void stop() {
        this.locationInquirer.stop();
        this.locationInquirer = null;
        this.locationBroadcaster = null;
    }
}
