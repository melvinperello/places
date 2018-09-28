package com.jhmvin.places.feature.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * Google Fused Location based location API.
 */
public class FusedLocationInquirer extends AbstractLocationInquirer {

    private final FusedLocationCallbck fusedLocationCallbck = new FusedLocationCallbck();

    public FusedLocationInquirer(Context base) {
        super(base);
    }


    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(6000)
                .setFastestInterval(4000);
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
        LocationServices.getFusedLocationProviderClient(this.context)
                .requestLocationUpdates(this.getLocationRequest(),
                        this.fusedLocationCallbck, null);
    }

    @Override
    public void stop() {
        LocationServices.getFusedLocationProviderClient(this.context)
                .removeLocationUpdates(this.fusedLocationCallbck);
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
