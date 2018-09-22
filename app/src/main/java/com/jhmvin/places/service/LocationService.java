package com.jhmvin.places.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService {
    /**
     * The Host Activity.
     */
    private Activity activity;
    /**
     * Android Location Manager.
     */
    private LocationManager locationManager;
    /**
     * Google Fused Location API.
     */
    private FusedLocationProviderClient mFusedLocationClient;


    public LocationService(Activity activity) {
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void getLastKnownLocation(final LocationListener listener) {
        if (ContextCompat.checkSelfPermission(
                this.activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            return; // do nothing.
        }

        this.mFusedLocationClient.getLastLocation().addOnSuccessListener(this.activity, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                listener.onLocationChanged(location);
            }
        });

    }

    /**
     * Checks whether the device GPS is enabled.
     *
     * @return device GPS State.
     */
    public boolean isGPSEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Launch the settings for Location.
     */
    public void callLocationSettingsIntent() {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.activity.startActivity(callGPSSettingIntent);
    }


}
