package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jhmvin.places.PlacesNew;
import com.jhmvin.places.domain.message.ActionTravelCheckMessage;
import com.jhmvin.places.domain.message.LocationReceivedMessage;
import com.jhmvin.places.feature.location.GoogleFusedLocationClient;
import com.jhmvin.places.feature.location.LocationClient;
import com.jhmvin.places.feature.location.OnLocationObtained;
import com.jhmvin.places.util.TempTravelStream;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlacesMainService extends Service implements BedTime, OnLocationObtained {
    private final static String TAG = PlacesMainService.class.getCanonicalName();


    //----------------------------------------------------------------------------------------------
    // Intent Actions -- Service Start Command.
    //----------------------------------------------------------------------------------------------
    /**
     * Canoncal Name of this class for unique identification.
     */
    private final static String CANONICAL_NAME = PlacesMainService.class.getCanonicalName();
    /**
     * Intent Action to start location gathering.
     */
    public final static String ACTION_TRAVEL_START = CANONICAL_NAME + ".ACTION_TRAVEL_START";
    /**
     * Intent Action to stop location gatheriing.
     */
    public final static String ACTION_TRAVEL_STOP = CANONICAL_NAME + ".ACTION_TRAVEL_STOP";
    /**
     * Intent Action to check Location gathering.
     */
    public final static String ACTION_TRAVEL_CHECK = CANONICAL_NAME + ".ACTION_TRAVEL_CHECK";
    /**
     * Intent Action to get all recorded location in this session.
     */
    public final static String ACTION_GET_POINTS = CANONICAL_NAME + ".ACTION_GET_POINTS";
    //----------------------------------------------------------------------------------------------
    // Service Awake (BedTime).
    //----------------------------------------------------------------------------------------------
    /**
     * Is this service Awake ?
     */
    private boolean mServiceAwake = false;

    @Override
    public void sleepMode() {
        this.mServiceAwake = false;
    }

    @Override
    public void wakeMode() {
        this.mServiceAwake = true;
    }

    @Override
    public boolean isAwake() {
        return this.mServiceAwake;
    }

    /**
     * This service is not bound. Ignore this.
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //----------------------------------------------------------------------------------------------
    // Service Proper.
    //----------------------------------------------------------------------------------------------


    private List<Location> mReceivedLocations;
    // commands
    private final Map<String, IntentStartCommand> mIntentCommand = new HashMap<>();

    // Travel Check Variables.
    private String mCheckOrigin;
    private String mCheckDestination;
    private long mCheckStartTime;
    private boolean mCheckStarted;

    public PlacesMainService() {
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


    private class ActionTravelStartCommand implements IntentStartCommand {

        @Override
        public void start(Intent intent, int flags, int startId) {
            if (!mCheckStarted) { // if not started
                setCheckIndicators(intent); // started will be true
                createFusedLocationClient();
                TempTravelStream travelStream = new TempTravelStream(getApplicationContext());
                try {
                    travelStream.start("Somewher", "there", System.currentTimeMillis());
                } catch (IOException ex) {
                    ToastAdapter.show(getApplicationContext(), ex.getMessage(), ToastAdapter.ERROR);
                } finally {
                    travelStream.close();
                }
            }
            ToastAdapter.show(getApplicationContext(), "Travel Start Command", ToastAdapter.SUCCESS);
        }
    }

    private class ActionTravelStopCommand implements IntentStartCommand {

        @Override
        public void start(Intent intent, int flags, int startId) {

            stopFusedLocationClient();

            unsetCheckIndicators(); // trash check vars
            ToastAdapter.show(getApplicationContext(), "Travel Stop Command", ToastAdapter.ERROR);
        }
    }

    private class ActionTravelCheck implements IntentStartCommand {

        @Override
        public void start(Intent intent, int flags, int startId) {
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

        mIntentCommand.put(ACTION_GET_POINTS, new IntentStartCommand() {
            @Override
            public void start(Intent intent, int flags, int startId) {
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
                IntentStartCommand command = mIntentCommand.get(intent.getAction());
                if (command != null) {
                    command.start(intent, flags, startId);
                }
            }
        }
        return START_STICKY;
    }


    @Override
    public void onLocationObtained(Location location) {
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

//    private final LocationCallback mLocationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (mReceivedLocations != null) {
//                for (Location location : locationResult.getLocations()) {
//
//                }
//            }
//        }
//    };

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


    private LocationClient locationClient;

    /**
     * Starts the location request.
     */
    private void createFusedLocationClient() {

        if (locationClient != null) {
            if (locationClient.isLocationAware()) {
                Log.d(TAG, "Location Awareness Already Running. . . Stop if First!");
                return;
            }
        }

        Log.d(TAG, "Creating Client . . .");

        // IF NOT LOCATION AWARE

        this.mReceivedLocations = new LinkedList<>();
        locationClient = new GoogleFusedLocationClient(this);
        locationClient.setLocationCallback(this);
        Log.d(TAG, "Location Awareness Started.");
        locationClient.startLocationAwareness();


//        this.mGoogleFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        this.mGoogleFusedLocationClient.requestLocationUpdates(
//                LocationRequest.create()
//                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                        .setInterval(5000)
//                        .setFastestInterval(3000)
//                , this.mLocationCallback, null
//        );
    }

    /**
     * Stop the location request.
     */
    private void stopFusedLocationClient() {
        if (locationClient != null) {
            locationClient.stopLocationAwareness();
        }
//        this.mGoogleFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
//        if (this.mReceivedLocations != null) {
//
//            this.mReceivedLocations = null;
//        }
//        this.mGoogleFusedLocationClient = null;
        if (this.mReceivedLocations != null) {
            this.mReceivedLocations.clear();
            this.mReceivedLocations = null;
        }
        Log.d(TAG, "Location Awareness Stopped.");

    }


}
