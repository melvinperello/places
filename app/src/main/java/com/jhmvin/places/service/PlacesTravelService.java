package com.jhmvin.places.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.jhmvin.places.PlacesNew;
import com.jhmvin.places.domain.message.ActionTravelCheckMessage;
import com.jhmvin.places.domain.message.LocationReceivedMessage;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlacesTravelService extends Service {
    private final static String CANONICAL_NAME = PlacesTravelService.class.getCanonicalName();
    public final static String ACTION_TRAVEL_START = CANONICAL_NAME + ".ACTION_TRAVEL_START";
    public final static String ACTION_TRAVEL_STOP = CANONICAL_NAME + ".ACTION_TRAVEL_STOP";
    public final static String ACTION_TRAVEL_CHECK = CANONICAL_NAME + ".ACTION_TRAVEL_CHECK";

    //
    public final static String ACTION_GET_POINTS = CANONICAL_NAME + ".ACTION_GET_POINTS";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // location
    private FusedLocationProviderClient mGoogleFusedLocationClient;
    private List<Location> mReceivedLocations;
    // commands
    private final Map<String, IntentRunnableCommand> mIntentCommand = new HashMap<>();

    // Travel Check Variables.
    private String mCheckOrigin;
    private String mCheckDestination;
    private long mCheckStartTime;
    private boolean mCheckStarted;

    public PlacesTravelService() {
        this.initCommands();
        this.unsetCheckIndicators();
    }

    private void setCheckIndicators(Intent intent) {
        mCheckOrigin = intent.getExtras().getString(PlacesNew.EXTRA_PLACE_ORIGIN);
        mCheckDestination = intent.getExtras().getString(PlacesNew.EXTRA_PLACE_DESTINATION);
        mCheckStartTime = System.currentTimeMillis();
        mCheckStarted = true;
    }

    private void unsetCheckIndicators() {
        mCheckOrigin = null;
        mCheckDestination = null;
        mCheckStartTime = 0;
        mCheckStarted = false;
    }

    private class ActionTravelStartCommand implements IntentRunnableCommand {

        @Override
        public void run(Intent intent, int flags, int startId) {
            if (!mCheckStarted) { // if not started
                setCheckIndicators(intent); // started will be true
                createFusedLocationClient();
            }
            ToastAdapter.show(getApplicationContext(), "Travel Start Command", ToastAdapter.SUCCESS);
        }
    }

    private class ActionTravelStopCommand implements IntentRunnableCommand {

        @Override
        public void run(Intent intent, int flags, int startId) {
            if (mGoogleFusedLocationClient != null) {
                stopFusedLocationClient();
            }
            unsetCheckIndicators(); // trash check vars
            ToastAdapter.show(getApplicationContext(), "Travel Stop Command", ToastAdapter.ERROR);
        }
    }

    private class ActionTravelCheck implements IntentRunnableCommand {

        @Override
        public void run(Intent intent, int flags, int startId) {
            ActionTravelCheckMessage check = new ActionTravelCheckMessage();
            check.setOrigin(mCheckOrigin);
            check.setDestination(mCheckDestination);
            check.setStartedTime(mCheckStartTime);
            check.setStarted(mCheckStarted);
            EventBus.getDefault().post(check);
        }
    }


    private void initCommands() {
        mIntentCommand.put(ACTION_TRAVEL_START, new ActionTravelStartCommand());
        mIntentCommand.put(ACTION_TRAVEL_STOP, new ActionTravelStopCommand());
        mIntentCommand.put(ACTION_TRAVEL_CHECK, new ActionTravelCheck());

        //

        mIntentCommand.put(ACTION_GET_POINTS, new IntentRunnableCommand() {
            @Override
            public void run(Intent intent, int flags, int startId) {
                if (mReceivedLocations != null) {
                    EventBus.getDefault().post(mReceivedLocations);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                IntentRunnableCommand command = mIntentCommand.get(intent.getAction());
                if (command != null) {
                    command.run(intent, flags, startId);
                }
            }
        }
        return START_STICKY;
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (mReceivedLocations != null) {
                for (Location location : locationResult.getLocations()) {
                    mReceivedLocations.add(location);

                    LocationReceivedMessage locMessage = new LocationReceivedMessage();
                    locMessage.setLongitude(location.getLongitude());
                    locMessage.setLatitude(location.getLatitude());
                    locMessage.setSpeed(location.getSpeed());
                    locMessage.setAccuracy(location.getAccuracy());

                    long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
                    long locTime = location.getElapsedRealtimeNanos() / 1000000;
                    long timeRecordedMills = bootTime + locTime;


                    locMessage.setTime(timeRecordedMills);


                    EventBus.getDefault().post(locMessage);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastAdapter.show(getApplicationContext(), "Service Was Destroyed: " + new SimpleDateFormat("hh:mm:ss a").format(new Date()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToastAdapter.show(getApplicationContext(), "Service Was Created: " + new SimpleDateFormat("hh:mm:ss a").format(new Date()));
    }


    /**
     * Starts the location request.
     */
    private void createFusedLocationClient() {
        this.mReceivedLocations = new LinkedList<>();
        this.mGoogleFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                , this.mLocationCallback, null
        );
    }

    /**
     * Stop the location request.
     */
    private void stopFusedLocationClient() {
        this.mGoogleFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        if (this.mReceivedLocations != null) {
            this.mReceivedLocations.clear();
            this.mReceivedLocations = null;
        }
        this.mGoogleFusedLocationClient = null;
    }


}
