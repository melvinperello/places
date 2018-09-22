package com.jhmvin.places.appservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.jhmvin.places.Places;
import com.jhmvin.places.R;
import com.jhmvin.places.service.FineLocationUpdateService;

public class LocationFService extends Service {
    private static final int NOTIFICATION_ID = 1;

    private FineLocationUpdateService fineLocationUpdateService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showForegroundNotification("Places ! Capturing Moments");
        this.fineLocationUpdateService = new FineLocationUpdateService(this);
        this.fineLocationUpdateService.startLocationUpdates(new OnReceiveLocationUpdate());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    private void displaylocation(Location location) {
        if (!this.fineLocationUpdateService.isGPSEnabled()) {
            Toast.makeText(this, "GPS Disabled Service Ending", Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }

        if (location == null) {
            Toast.makeText(this, "No Location Data", Toast.LENGTH_SHORT).show();
            return;
        }

        String accuracy = String.valueOf(location.getAccuracy());
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String time = String.valueOf(location.getElapsedRealtimeNanos() / 1000000L);
        String text = String.format("Time: %s Accuracy: %s -- lat: %s -- lng: %s", time, accuracy, lat, lng);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showForegroundNotification(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), Places.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed.", Toast.LENGTH_SHORT).show();

        /**
         * Remove Notification.
         */
        this.stopForeground(true);
        this.fineLocationUpdateService.stopLocationUpdates();
        /**
         * Stop Service.
         */
        this.stopSelf();
    }

    private class OnReceiveLocationUpdate extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                displaylocation(locationResult.getLastLocation());
            } else {
                Log.d("Location", "No Location Results");
            }
        }
    }


}
