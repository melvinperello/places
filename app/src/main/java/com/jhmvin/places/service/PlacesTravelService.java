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
import com.jhmvin.places.domain.message.CheckItineraryMessage;
import com.jhmvin.places.domain.message.LocationReceivedMessage;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PlacesTravelService extends Service {

    public final static String ACTION_START_TRAVEL = PlacesTravelService.class.getCanonicalName() + ".ACTION_START_TRAVEL";
    public final static String ACTION_STOP_TRAVEL = PlacesTravelService.class.getCanonicalName() + ".ACTION_STOP_TRAVEL";
    public final static String ACTION_CHECK_ITINERARY = PlacesTravelService.class.getCanonicalName() + ".ACTION_CHECK_ITINERARY";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private FusedLocationProviderClient mGoogleFusedLocationClient;
    private List<Location> mReceivedLocations;


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


    private String mOrigin;
    private String mDestination;
    private long mStarted = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_START_TRAVEL)) {
                    if (this.mGoogleFusedLocationClient == null) {
                        this.createFusedLocationClient();
                        mOrigin = intent.getExtras().getString(PlacesNew.EXTRA_PLACE_ORIGIN);
                        mDestination = intent.getExtras().getString(PlacesNew.EXTRA_PLACE_DESTINATION);
                        mStarted = System.currentTimeMillis();
                    }
                } else if (intent.getAction().equals(ACTION_STOP_TRAVEL)) {
                    if (this.mGoogleFusedLocationClient != null) {
                        this.stopFusedLocationClient();
                        this.stopSelf();
                    }
                } else if (intent.getAction().equals(ACTION_CHECK_ITINERARY)) {
                    CheckItineraryMessage itinerary = new CheckItineraryMessage();
                    itinerary.setOrigin(this.mOrigin);
                    itinerary.setDestination(mDestination);
                    itinerary.setStarted(mStarted);
                    EventBus.getDefault().post(itinerary);
                }
            }
        }
        return START_STICKY;
    }

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

    private void stopFusedLocationClient() {
        this.mGoogleFusedLocationClient.removeLocationUpdates(this.mLocationCallback);
        if (this.mReceivedLocations != null) {
            this.mReceivedLocations.clear();
            this.mReceivedLocations = null;
        }
        this.mGoogleFusedLocationClient = null;
    }


}
