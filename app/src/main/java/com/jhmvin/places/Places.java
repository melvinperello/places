package com.jhmvin.places;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.jhmvin.places.service.LocationPermissionService;
import com.jhmvin.places.service.LocationService;

public class Places extends AppCompatActivity {


    private LocationPermissionService locationPermissionService;
    private LocationService locationService;

    private Button btnCheckGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        /**
         * Create Location Permission Service.
         */
        this.locationPermissionService = new LocationPermissionService(this);
        // ask if no permission
        if (!this.locationPermissionService.isFineLocationAllowed()) {
            this.locationPermissionService.requestForFineLocation();
        }

        //
        this.locationService = new LocationService(this);

        this.btnCheckGPS = (Button) this.findViewById(R.id.btnStart);
        this.btnCheckGPS.setOnClickListener(new ClickCheckGPS());

    }

    private void finishActivityNoPermission() {
        Toast.makeText(this, "Location Access Denied Apllication will end.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    /**
     * check permission request.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LocationPermissionService.REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    finishActivityNoPermission();
                }
                return;
            }
        }
    }

    private void checkIfReady() {
        if (this.locationService.isGPSEnabled()) {
            this.locationService.getLastKnownLocation(new LastKnownLocation());
        } else {
            Toast.makeText(this, "Requires GPS Permission", Toast.LENGTH_SHORT).show();
            this.locationService.callLocationSettingsIntent();
        }
    }

    private void displaylocation(Location location) {
        if(location == null){
            Toast.makeText(this,"No Location Data",Toast.LENGTH_SHORT).show();
            return;
        }

        String accuracy = String.valueOf(location.getAccuracy());
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String time = String.valueOf(location.getElapsedRealtimeNanos() / 1000000L);
        String text = String.format("Time: %s Accuracy: %s -- lat: %s -- lng: %s", time, accuracy, lat, lng);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private class LastKnownLocation implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            displaylocation(location);
        }
    }

    private class ClickCheckGPS implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            checkIfReady();
        }
    }
}
