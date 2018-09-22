package com.jhmvin.places.service;

import android.app.Service;
import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Manage All the Location Logic of this application.
 * Requires ACCESS_FINE_LOCATION.
 */
public class FineLocationUpdateService {
    /**
     * Activity Context.
     */
    private Service service;
    /**
     * Android Location Manager.
     */
    private LocationManager locationManager;
    /**
     * Google Fused Location API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Location Update Callback.
     */
    private LocationCallback mLocationCallback;

    /**
     * Location Request Settings for updates.
     */
    private LocationRequest mLocationRequest;

    /**
     * Default Constructor. requires service.
     *
     * @param service
     */
    public FineLocationUpdateService(Service service) {
        // Android Activity.
        this.service = service;
        // get location manager
        this.locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
        // get google play services fused location.
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(service);
        // setup location request
        this.mLocationRequest = this.buildLocationRequest();
    }

    /**
     * Creates the location request.
     *
     * @return
     */
    private LocationRequest buildLocationRequest() {
        /**
         * Location gathering and battery drain are directly related in the following aspects:
         * Accuracy: The precision of the location data. In general, the higher the accuracy, the higher the battery drain.
         * Frequency: How often location is computed. The more frequent location is computed, the more battery is used.
         * Latency: How quickly location data is delivered. Less latency usually requires more battery.
         *
         * https://developer.android.com/guide/topics/location/battery
         */
        LocationRequest locationRequest = new LocationRequest();
        /**
         * PRIORITY_HIGH_ACCURACY provides the most accurate location possible, which is computed using as many inputs as necessary (it enables GPS, Wi-Fi, and cell, and uses a variety of Sensors), and may cause significant battery drain.
         * PRIORITY_BALANCED_POWER_ACCURACY provides accurate location while optimizing for power. Very rarely uses GPS. Typically uses a combination of Wi-Fi and cell information to compute device location.
         * PRIORITY_LOW_POWER largely relies on cell towers and avoids GPS and Wi-Fi inputs, providing coarse (city-level) accuracy with minimal battery drain.
         * PRIORITY_NO_POWER receives locations passively from other apps for which location has already been computed.
         */
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**
         * method to specify the interval at which location is computed for your app.
         * method depends on the use case: for real time scenarios, set the value to few seconds; otherwise, limit to a few minutes (approximately two minutes or greater is recommended to minimize battery usage).
         */
        locationRequest.setInterval(2 * 60 * 1000); // 2 minute
        /**
         * to specify the interval at which location computed for other apps is delivered to your app.
         */
        locationRequest.setFastestInterval(1 * 60 * 1000); // 1 minute
        /**
         * You can specify latency using the setMaxWaitTime() method, typically passing a value that is several times larger than the interval specified in the setInterval() method. This setting delays location delivery, and multiple location updates may be delivered in batches. These two changes help minimize battery consumption.
         * If your app doesn’t immediately need a location update, you should pass the largest possible value to the setMaxWaitTime() method, effectively trading latency for more data and battery efficiency.
         */
        locationRequest.setMaxWaitTime(4 * 60 * 1000); // 4 minute

        return locationRequest;
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


    public void startLocationUpdates(LocationCallback mLocationCallback) {
        if (mLocationCallback == null) {
            return;
        }

        this.mLocationCallback = mLocationCallback;
        try {
            this.mFusedLocationClient.requestLocationUpdates(this.mLocationRequest, mLocationCallback, null);
        } catch (SecurityException se) {
            Log.e("Security Exception", "Fine Location Required");
        }
    }

    public void stopLocationUpdates() {
        this.mLocationCallback = null;
    }


}
