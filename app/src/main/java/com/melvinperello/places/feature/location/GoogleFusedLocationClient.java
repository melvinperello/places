package com.melvinperello.places.feature.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Google Fused Location Client.
 */
public class GoogleFusedLocationClient implements LocationClient {

    /**
     * Service Context.
     */
    private final Context mContext;
    /**
     * User callback on locations obtained.
     */
    private OnLocationObtained mUserDefinedCallback;

    private boolean mLocationAwareness = false;

    /**
     * Create the client with the context.
     *
     * @param mContext context from application.
     */
    public GoogleFusedLocationClient(final Context mContext) {
        this.mContext = mContext;
    }


    /**
     * Google Fused Location Client.
     */
    private FusedLocationProviderClient mGoogleFusedLocationClient;
    /**
     * Create google compatible callback that will call the user defined callback.
     */
    private final GoogleLocationCallback mGoogleCallback = new GoogleLocationCallback();

    /**
     * Google Callback.
     */
    private class GoogleLocationCallback extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            // check for null.
            if (mUserDefinedCallback != null) {
                // loop all available location.
                for (Location location : locationResult.getLocations()) {
                    // call user-defined callback that a location is available.
                    mUserDefinedCallback.onLocationObtained(location);
                }
            }
        }
    }


    /**
     * Start Location Awareness.
     */
    @Override
    public void startLocationAwareness() {
        this.mGoogleFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.mContext);
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.mGoogleFusedLocationClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(5000)
                        .setFastestInterval(3000)
                , mGoogleCallback, null
        );
        mLocationAwareness = true;
    }

    @Override
    public void setLocationCallback(OnLocationObtained callback) {
        this.mUserDefinedCallback = callback;
    }


    @Override
    public void stopLocationAwareness() {
        // check if the client is not null
        if (this.mGoogleFusedLocationClient != null) {
            // remove google call back
            this.mGoogleFusedLocationClient.removeLocationUpdates(mGoogleCallback);
            // unset user callback
            this.mUserDefinedCallback = null;
            // unset the client.
            this.mGoogleFusedLocationClient = null;
        }

        mLocationAwareness = false;
    }

    @Override
    public boolean isLocationAware() {
        return mLocationAwareness;
    }
}
