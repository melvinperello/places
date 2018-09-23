package com.jhmvin.places.service;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationUpdateRequestService {

    //----------------------------------------------------------------------------------------------
    // Service Variables.
    //----------------------------------------------------------------------------------------------

    private Context context;
    private LocationRequest moderateLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback onLocationReceived;

    public LocationUpdateRequestService(Context context) {
        this.context = context;
        this.moderateLocationRequest = LocationRequest.create()
                /**
                 * PRIORITY_HIGH_ACCURACY provides the most accurate location possible, which is computed using as many inputs as necessary (it enables GPS, Wi-Fi, and cell, and uses a variety of Sensors), and may cause significant battery drain.
                 * PRIORITY_BALANCED_POWER_ACCURACY provides accurate location while optimizing for power. Very rarely uses GPS. Typically uses a combination of Wi-Fi and cell information to compute device location.
                 * PRIORITY_LOW_POWER largely relies on cell towers and avoids GPS and Wi-Fi inputs, providing coarse (city-level) accuracy with minimal battery drain.
                 * PRIORITY_NO_POWER receives locations passively from other apps for which location has already been computed.
                 */
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                /**
                 * method to specify the interval at which location is computed for your app.
                 * method depends on the use case: for real time scenarios, set the value to few seconds; otherwise, limit to a few minutes (approximately two minutes or greater is recommended to minimize battery usage).
                 */
                .setInterval(30000) // 30 seconds.
                /**
                 * to specify the interval at which location computed for other apps is delivered to your app.
                 */
                .setFastestInterval(15000) // 15 seconds.
                /**
                 * You can specify latency using the setMaxWaitTime() method, typically passing a value that is several times larger than the interval specified in the setInterval() method. This setting delays location delivery, and multiple location updates may be delivered in batches. These two changes help minimize battery consumption.
                 * If your app doesn’t immediately need a location update, you should pass the largest possible value to the setMaxWaitTime() method, effectively trading latency for more data and battery efficiency.
                 */
                .setMaxWaitTime(60000); // 60 seconds.
        // google fused location client
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        // since this is jusst a test, add a callback.
        this.onLocationReceived = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    displaylocation(locationResult.getLastLocation());
                } else {
                    displayNoLocation();
                }
            }
        };
    }

    //----------------------------------------------------------------------------------------------
    // Service Methods.
    //----------------------------------------------------------------------------------------------

    private void displayNoLocation() {
        Toast.makeText(this.context, "PlacesTrackingService: No Location", Toast.LENGTH_SHORT).show();
    }

    private void displaylocation(Location location) {
        if (location == null) {
            return;
        }

        String accuracy = String.valueOf(location.getAccuracy());
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
//        String time = String.valueOf(location.getElapsedRealtimeNanos() / 1000000L);
        String speed = String.valueOf(location.getSpeed());
        String provider = String.valueOf(location.getProvider());


        String text = String.format("Provider: [%s] Speed: %s m/s -- Accuracy: %s -- lat: %s -- lng: %s", provider, speed, accuracy, lat, lng);
        Toast.makeText(this.context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Set Location Callback.
     *
     * @param onLocationReceived
     */
    public void setOnLocationReceived(LocationCallback onLocationReceived) {
        this.onLocationReceived = onLocationReceived;
    }

    public void startLocationRequest() {
        if (this.onLocationReceived == null) {
            return;
        }

        try {
            this.mFusedLocationClient.requestLocationUpdates(
                    this.moderateLocationRequest,
                    this.onLocationReceived,
                    null
            );
        } catch (SecurityException se) {
            Toast.makeText(this.context, "Cannot Receive Location, Security Exception", Toast.LENGTH_SHORT).show();
        }
    }


    public void stopLocationRequest() {
        if (this.onLocationReceived != null) {
            this.mFusedLocationClient.removeLocationUpdates(this.onLocationReceived);
        }
    }
}
