package com.jhmvin.places.service;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class LocationPermissionService {

    public static final int REQUEST_FINE_LOCATION = 100;

    private Activity activity;



    public LocationPermissionService(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check whether the application has GPS Access.
     *
     * @return GPS is allowed?
     */
    public boolean isFineLocationAllowed() {
        return ContextCompat.checkSelfPermission(
                this.activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
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

    public void requestForFineLocation(){
        ActivityCompat.requestPermissions(this.activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LocationPermissionService.REQUEST_FINE_LOCATION);
    }


}
