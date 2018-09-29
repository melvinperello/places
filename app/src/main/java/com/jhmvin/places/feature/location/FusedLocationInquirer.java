package com.jhmvin.places.feature.location;

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
 * Google Fused Location based location API.
 */
public class FusedLocationInquirer extends AbstractLocationInquirer {

    private FusedLocationCallbck fusedLocationCallbck;
    private FusedLocationProviderClient googleFusedLocationClient;

    public FusedLocationInquirer(Context base) {
        super(base);


    }


    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(3000);
    }


    @Override
    public void start() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // make sure that the previous call has stopped if there is any.
        this.stop();

        // re-create the service.
        this.fusedLocationCallbck = new FusedLocationCallbck();
        this.googleFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);

        // start callback.
        this.googleFusedLocationClient
                .requestLocationUpdates(this.getLocationRequest(),
                        this.fusedLocationCallbck, null);
    }

    /**
     * Ensures that the Fused Location API has shutdown properly.
     */
    @Override
    public void stop() {
        // check if null.
        if (this.googleFusedLocationClient == null) {
            return;
        }
        // check if has callback
        if (this.fusedLocationCallbck == null) {
            return;
        }

        // remove callback
        this.googleFusedLocationClient
                .removeLocationUpdates(this.fusedLocationCallbck);
        // set to null.
        this.googleFusedLocationClient = null;
        this.fusedLocationCallbck = null;
    }

    private class FusedLocationCallbck extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (onLocationUpdated != null) {
                for (Location location : locationResult.getLocations()) {
                    onLocationUpdated.onLocationUpdated(location);
                }
            }

        }
    }


}
