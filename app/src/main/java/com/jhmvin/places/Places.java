package com.jhmvin.places;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jhmvin.places.service.DeviceLocationCheckService;
import com.jhmvin.places.service.PlacesTrackingService;

/**
 * Android Activity Lifecycle.
 * 1. onCreate
 * 2. onStart
 * 3. onResume
 * 4. Activity Running
 * 5. onPause
 * 6. onStop - when attempting to destroy the activity.
 * 7. onDestroy
 */
public class Places extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    // Control Variables.
    //----------------------------------------------------------------------------------------------
    private Button btnCheckGPS;
    private Button btnCheckService;
    private Button btnStartService;
    private Button btnStopService;

    //----------------------------------------------------------------------------------------------
    // Activity Variables.
    //----------------------------------------------------------------------------------------------

    private View.OnClickListener buttonClickListener;
    private DeviceLocationCheckService deviceLocationCheck;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver activityBroadcastListener;


    //----------------------------------------------------------------------------------------------
    // 1. Create.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);


        // controls definition.
        this.connectControlsToView();
        // add events
        this.addControlEvents();

        // checking service
        this.deviceLocationCheck = new DeviceLocationCheckService(this);
        // get local broadcast manager.
        this.localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        // create broadcast listener for this activity.
        this.activityBroadcastListener = new PlacesActivityBroadcastListener();
    }

    /**
     * Maps controls to view.
     */
    private void connectControlsToView() {
        this.btnCheckGPS = (Button) this.findViewById(R.id.btnCheckGPS);
        this.btnCheckService = (Button) this.findViewById(R.id.btnCheckService);
        this.btnStartService = (Button) this.findViewById(R.id.btnStartService);
        this.btnStopService = (Button) this.findViewById(R.id.btnStopService);
    }

    /**
     * Add Events.
     */
    private void addControlEvents() {
        // create button listener
        this.buttonClickListener = new ButtonClickListener();
        this.btnCheckGPS.setOnClickListener(this.buttonClickListener);
        this.btnCheckService.setOnClickListener(this.buttonClickListener);
        this.btnStartService.setOnClickListener(this.buttonClickListener);
        this.btnStopService.setOnClickListener(this.buttonClickListener);
    }

    //----------------------------------------------------------------------------------------------
    // 2. Start.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {


        super.onStart();
    }

    //----------------------------------------------------------------------------------------------
    // 3. Resume.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        // Register Broadcast Listener.
        this.registerBroadcastListener();
        // ping service to check if its alive.
        this.sendPingMessageToPlacesTrackingService();
    }

    private void registerBroadcastListener() {
        IntentFilter actionFilter = new IntentFilter();
        actionFilter.addAction(PlacesTrackingService.REPLY_ALIVE);
        this.localBroadcastManager.registerReceiver(this.activityBroadcastListener, actionFilter);
    }


    //----------------------------------------------------------------------------------------------
    // 5. Puase.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
        // unregister
        this.unregisterBroadcastListener();
    }

    private void unregisterBroadcastListener() {
        this.localBroadcastManager.unregisterReceiver(this.activityBroadcastListener);
    }

    //----------------------------------------------------------------------------------------------
    // 6. Stop.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        // share with on Pause event.
        this.unregisterBroadcastListener();
    }

    //----------------------------------------------------------------------------------------------
    // 7. Destroy.
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------------
    // Activity Methods.
    //----------------------------------------------------------------------------------------------

    private void sendStopMessageToPlacesTrackingService() {
        this.btnStartService.setEnabled(true);
        //
        this.localBroadcastManager.sendBroadcast(new Intent(PlacesTrackingService.ACTION_STOP_SERVICE));
    }

    private void sendPingMessageToPlacesTrackingService() {
        // the service will respond to this broadcast only if it's running
        this.localBroadcastManager.sendBroadcast(new Intent(PlacesTrackingService.ASK_ALIVE));
    }

    /**
     * If ping has reply to the service this method will be called.
     */
    private void onPlacesTrackingServiceAlive() {
        this.btnStartService.setEnabled(false);
        Toast.makeText(this, "Service is running", Toast.LENGTH_SHORT).show();
    }


    private void startPlacesTrackingService() {
        if (this.deviceLocationCheck.isPermissionFineLocationAllowed()) {
            this.btnStartService.setEnabled(false);
            Thread locationUpdateServiceThread = new Thread() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), PlacesTrackingService.class);
                    startService(intent);
                }
            };
            locationUpdateServiceThread.start();

        }
    }

    private void openLocationSettings() {
        this.deviceLocationCheck.openLocationSettings();
    }

    //----------------------------------------------------------------------------------------------
    // Runtime Permission Request.
    //----------------------------------------------------------------------------------------------

//    /**
//     * check permission request.
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case FineLocationManager.REQUEST_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//                    finishActivityNoPermission();
//                }
//                return;
//            }
//        }
//    }


    //----------------------------------------------------------------------------------------------
    // Inner Classes.
    //----------------------------------------------------------------------------------------------

    /**
     * Button Events inner class.
     */
    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.equals(btnCheckGPS)) {
                openLocationSettings();
            } else if (v.equals(btnCheckService)) {
                sendPingMessageToPlacesTrackingService();
            } else if (v.equals(btnStartService)) {
                startPlacesTrackingService();
            } else if (v.equals(btnStopService)) {
                sendStopMessageToPlacesTrackingService();
            }
        }
    }

    private class PlacesActivityBroadcastListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // here you receive the response from the service
            if (intent.getAction().equals(PlacesTrackingService.REPLY_ALIVE)) {
                onPlacesTrackingServiceAlive();
            }
        }
    }


}
