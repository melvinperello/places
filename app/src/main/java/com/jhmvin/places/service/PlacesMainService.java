package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jhmvin.places.feature.location.AbstractLocationInquirer;
import com.jhmvin.places.feature.location.FusedLocationInquirer;
import com.jhmvin.places.persistence.LocationPOJO;
import com.sdsmdg.tastytoast.TastyToast;

/**
 * Application Main Service.
 * <p>
 * Serves as a Interface for multiple service.
 * <p>
 * 1. Location Service.
 */
public class PlacesMainService extends Service {


    public final static String START_LOCATION_SERVICE = "START_LOCATION_SERVICE";
    public final static String STOP_LOCATION_SERVICE = "STOP_LOCATION_SERVICE";

    //----------------------------------------------------------------------------------------------
    // Bindings.
    //----------------------------------------------------------------------------------------------
    private final IBinder mBinder = new PlacesMainServiceBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Local Binder.
     */
    public class PlacesMainServiceBinder extends Binder {
        public PlacesMainService getService() {
            return PlacesMainService.this;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Service Variables.
    //----------------------------------------------------------------------------------------------
    private AbstractLocationInquirer locationInquirer;
    private final OnLocationReceived onLocationReceived = new OnLocationReceived();
    private boolean requestingLocations = false;
//    private ObservableArrayList<LocationPOJO> locationList;

    private class OnLocationReceived implements AbstractLocationInquirer.OnLocationUpdated {

        @Override
        public void onLocationUpdated(Location location) {
            LocationPOJO locationPOJO = new LocationPOJO(location);
//            locationList.add(locationPOJO);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Service Created.
    //----------------------------------------------------------------------------------------------
    @Override
    public void onCreate() {
        super.onCreate();
        this.locationInquirer = new FusedLocationInquirer(this);
        this.locationInquirer.setOnLocationUpdated(this.onLocationReceived);

        TastyToast.makeText(getApplicationContext(), "Main Service Started", TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(START_LOCATION_SERVICE)) {
                startRequestingLocations();
            }
        }
        /**
         * Restart automatically.
         */
        return START_STICKY;
    }

    public void startRequestingLocations() {
//        locationList = new ObservableArrayList<>();
        this.locationInquirer.start();
        this.requestingLocations = true;
    }

//    public ObservableArrayList<LocationPOJO> getLocationList() {
//        return locationList;
//    }

    public void stopRequestingLocations() {
        this.locationInquirer.stop();
        this.requestingLocations = false;
//        if (locationList != null) {
//            locationList.clear();
//            locationList = null;
//        }
    }

    public boolean isRequestingLocations() {
        return requestingLocations;
    }

    @Deprecated
    private void displaylocation(Location location) {
        if (location == null) {
            return;
        }

        String accuracy = String.valueOf(location.getAccuracy());
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
//        String time = String.valueOf(location.getElapsedRealtimeNanos() / 1000000L);
        String speed = String.valueOf(location.getSpeed());
        String provider = String.valueOf(location.getProvider());


        String text = String.format("Provider: [%s] Speed: %s m/s -- Accuracy: %s -- lat: %s -- lng: %s", provider, speed, accuracy, lat, lng);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
