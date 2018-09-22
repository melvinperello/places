package com.jhmvin.places.service;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class FineLocationManager {
    public final static int REQUEST_FINE_LOCATION = 100;

    private Activity activity;

    public FineLocationManager(Activity activity) {
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
     * Open Settings activity for location, this will easily allow user's to enable GPS.
     */
    public void openLocationSettings() {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.activity.startActivity(callGPSSettingIntent);
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
                FineLocationManager.REQUEST_FINE_LOCATION);
    }
}
