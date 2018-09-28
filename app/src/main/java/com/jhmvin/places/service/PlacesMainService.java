package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jhmvin.places.feature.Serviceable;
import com.jhmvin.places.feature.location.LocationService;

public class PlacesMainService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Serviceable locationService;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    private void startLocationBroadcast() {
        this.locationService = new LocationService(this);
    }

    private void stopLocationBroadcast() {
        this.locationService.stop();
    }
}
