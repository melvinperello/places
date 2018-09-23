package com.jhmvin.places.service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;

/**
 * Checks the capabilities and permission of the device to use Location.
 * <p>
 * Requires context.
 */
public class DeviceLocationCheckService {


    //----------------------------------------------------------------------------------------------
    // Service Variables.
    //----------------------------------------------------------------------------------------------

    private Context context;
    private LocationManager locationManager;

    public DeviceLocationCheckService(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    //----------------------------------------------------------------------------------------------
    // Service Methods.
    //----------------------------------------------------------------------------------------------

    /**
     * Open Settings context for location, this will easily allow user to enable GPS.
     */
    public void openLocationSettings() {
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        this.context.startActivity(callGPSSettingIntent);
    }

    /**
     * Checks whether the device GPS is enabled. this is different from location access from mobile
     * or cellular methods.
     *
     * @return device GPS State.
     */
    public boolean isGPSEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    //----------------------------------------------------------------------------------------------
    // Marshmallow Runtime Permission Checks.
    //----------------------------------------------------------------------------------------------

    /**
     * Check whether the application has GPS Access.
     *
     * @return GPS is allowed?
     */
    public boolean isPermissionFineLocationAllowed() {
        return ContextCompat.checkSelfPermission(
                this.context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check whether the application has coarse location access.
     *
     * @return GPS is allowed?
     */
    public boolean isPermissionCoarseLocationAllowed() {
        return ContextCompat.checkSelfPermission(
                this.context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


}
