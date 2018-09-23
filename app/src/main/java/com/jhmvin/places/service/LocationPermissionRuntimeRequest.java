package com.jhmvin.places.service;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * Request permission during runtime.
 * <p>
 * Marshmallow and up.
 */
public class LocationPermissionRuntimeRequest {
    //----------------------------------------------------------------------------------------------
    // Request Codes.
    //----------------------------------------------------------------------------------------------

    public final static int REQUEST_FINE_LOCATION = 100;

    //----------------------------------------------------------------------------------------------
    // Service Variables.
    //----------------------------------------------------------------------------------------------
    private Activity activity;

    public LocationPermissionRuntimeRequest(Activity activity) {
        this.activity = activity;
    }

    /**
     * Is this the first time to ask permission.
     *
     * @return
     */
    public boolean isFineLocationFirstAsked() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this.activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void requestFineLocation() {
        ActivityCompat.requestPermissions(this.activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LocationPermissionRuntimeRequest.REQUEST_FINE_LOCATION);
    }
}
