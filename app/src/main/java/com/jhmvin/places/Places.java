package com.jhmvin.places;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jhmvin.places.service.PlacesTrackService;
import com.jhmvin.places.service.FineLocationManager;

public class Places extends AppCompatActivity {
    private FineLocationManager fineLocationManager;

    private Button btnCheckGPS;
    private Button btnBroadcast;

    private LocationServiceReceiver mReceiver = new LocationServiceReceiver();
    private LocalBroadcastManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        this.fineLocationManager = new FineLocationManager(this);
        // ask if no permission
        if (!this.fineLocationManager.isFineLocationAllowed()) {
            this.fineLocationManager.requestFineLocation();
        }

        this.manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.registerReceiver(this.mReceiver, new IntentFilter(PlacesTrackService.ACTION_PONG));

        this.btnCheckGPS = (Button) this.findViewById(R.id.btnStart);
        this.btnCheckGPS.setOnClickListener(new ClickCheckGPS());
        this.btnBroadcast = (Button) this.findViewById(R.id.btnBroadcast);
        this.btnBroadcast.setOnClickListener(new ClickBroadcast());

    }

    @Override
    protected void onStart() {


        super.onStart();
    }

    private void sendMessage() {
        // the service will respond to this broadcast only if it's running
        manager.sendBroadcast(new Intent(PlacesTrackService.ACTION_PING));
    }


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mReceiver);
        super.onStop();
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
            Thread locationUpdateServiceThread = new Thread() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), PlacesTrackService.class);
                    startService(intent);
                }
            };
            locationUpdateServiceThread.start();
        }
    }

    private void checkService() {
        Toast.makeText(this, "Service is running", Toast.LENGTH_SHORT).show();
    }

    private class LocationServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // here you receive the response from the service
            if (intent.getAction().equals(PlacesTrackService.ACTION_PONG)) {
                checkService();
            }
        }
    }

    private class ClickCheckGPS implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            checkIfReady();
        }
    }

    private class ClickBroadcast implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            sendMessage();
        }
    }
}
