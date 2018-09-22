package com.jhmvin.places;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jhmvin.places.appservice.LocationFService;
import com.jhmvin.places.service.FineLocationManager;

public class Places extends AppCompatActivity {
    private FineLocationManager fineLocationManager;

    private Button btnCheckGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        this.fineLocationManager = new FineLocationManager(this);
        // ask if no permission
        if (!this.fineLocationManager.isFineLocationAllowed()) {
            this.fineLocationManager.requestFineLocation();
        }

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
            case FineLocationManager.REQUEST_FINE_LOCATION: {
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
        if (this.fineLocationManager.isFineLocationAllowed()) {
            Intent intent = new Intent(this, LocationFService.class);
            startService(intent);
        }
    }


    private class ClickCheckGPS implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            checkIfReady();
        }
    }
}
