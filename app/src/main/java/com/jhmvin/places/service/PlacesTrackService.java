package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Location Update Service, this service must be in foreground and therefore must maintain
 * notification display for its activity.
 */
public class PlacesTrackService extends Service {
    //    public static final String ACTION_PING = PlacesTrackService.class.getName() + ".PING";
//    public static final String ACTION_PONG = PlacesTrackService.class.getName() + ".PONG";
//
//    public final static String IS_RUNNING = PlacesTrackService.class.getCanonicalName() + ".IS_RUNNING";
//
//    private static final int NOTIFICATION_ID = 1;
//    private FineLocationUpdateService fineLocationUpdateService;
//    private BroadcastReceiver locationServiceBroadcastListener = new LocationServiceBroadcastListener();
//
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
//
//    /**
//     * onCreate() is called when the Service object is instantiated
//     * (ie: when the service is created). You should do things in this method that you need to do
//     * only once (ie: initialize some variables, etc.). onCreate() will only ever be called once
//     * per instantiated object.
//     * <p>
//     * https://stackoverflow.com/questions/14182014/android-oncreate-or-onstartcommand-for-starting-service
//     */
//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }
//
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Toast.makeText(this, "Location Update Service: Terminated", Toast.LENGTH_SHORT).show();
//
//        /**
//         * Remove Notification.
//         */
//        this.stopForeground(true);
//        this.fineLocationUpdateService.stopLocationUpdates();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.locationServiceBroadcastListener);
//    }
//
//    /**
//     * onStartCommand() is called every time a client starts the service using
//     * startService(Intent intent). This means that onStartCommand() can get called multiple times.
//     * You should do the things in this method that are needed each time a client requests something
//     * from your service. This depends a lot on what your service does and how it
//     * communicates with the clients (and vice-versa).
//     * <p>
//     * If you don't implement onStartCommand() then you won't be able to get any information from
//     * the Intent that the client passes to onStartCommand() and your service might not be able to
//     * do any useful work.
//     * <p>
//     * https://stackoverflow.com/questions/14182014/android-oncreate-or-onstartcommand-for-starting-service
//     *
//     * @param intent
//     * @param flags
//     * @param startId
//     * @return
//     */
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        /**
//         * Run Service Components.
//         */
//        this.initService();
//        /**
//         * START_STICKY means "Hey Android! If you really really have to shut down my precious Service because of running low on memory, then please please try to start it again."
//         * START_NOT_STICKY would mean "Nahh...don't bother. I'll call startService() again myself if I really need my Service running."
//         * This (start sticky) is probably fine most of time. Your Service will just start from scratch again. You could try if that's suitable for your use case.
//         */
//        return START_NOT_STICKY;
//    }
//
//    private void initService() {
//        this.registerBroadcastReceivers();
//        this.startLocationUpdates();
//
//        Toast.makeText(this, "Location Update Service: Started", Toast.LENGTH_SHORT).show();
//
//
//        /**
//         * Upon completing start events show notification.
//         */
//        this.showForegroundNotification();
//    }
//
//    private void registerBroadcastReceivers() {
//        LocalBroadcastManager.getInstance(this)
//                .registerReceiver(this.locationServiceBroadcastListener, new IntentFilter(ACTION_PING));
//    }
//
//    private void startLocationUpdates() {
//        this.fineLocationUpdateService = new FineLocationUpdateService(this);
//        this.fineLocationUpdateService.startLocationUpdates(new OnReceiveLocationUpdate());
//    }
//
//    /**
//     * This Service will listen to Context broadcast.
//     */
//    private class LocationServiceBroadcastListener extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ACTION_PING)) {
//                /**
//                 * IF received send a message.
//                 */
//                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
//                manager.sendBroadcast(new Intent(ACTION_PONG));
//            }
//        }
//    }
//
//    private void showForegroundNotification() {
//        // Create intent that will bring our app to the front, as if it was tapped in the app
//        // launcher
////        Intent showTaskIntent = new Intent(getApplicationContext(), Places.class);
////        showTaskIntent.setAction(Intent.ACTION_MAIN);
////        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
////        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////
////        PendingIntent contentIntent = PendingIntent.getActivity(
////                getApplicationContext(),
////                0,
////                showTaskIntent,
////                PendingIntent.FLAG_UPDATE_CURRENT);
////
////        Notification notification = new Notification.Builder(getApplicationContext())
////                .setContentTitle(getString(R.string.app_name))
////                .setContentText("Chasing moments.")
////                .setSmallIcon(R.mipmap.ic_launcher)
////                .setWhen(System.currentTimeMillis())
////                .setContentIntent(contentIntent)
////                .build();
////        startForeground(NOTIFICATION_ID, notification);
//
////        Notification notification = new Notification.Builder(getApplicationContext())
////                .setContentTitle("Places")
////                .setContentText("Chasing moments.")
////                .setSmallIcon(R.mipmap.ic_launcher)
////                .setPriority(Notification.PRIORITY_MAX)
////                .setOngoing(true)
////                .setAutoCancel(false)
////                .build();
//
//
////        startForeground(NOTIFICATION_ID, notification);
//    }
//
//
//
//    /**
//     * Display Location from update.
//     *
//     * @param location
//     */
//    private void displaylocation(Location location) {
//        if (!this.fineLocationUpdateService.isGPSEnabled()) {
//            Toast.makeText(this, "GPS Disabled Service Ending", Toast.LENGTH_SHORT).show();
//            this.stopSelf();
//        }
//
//        if (location == null) {
//            Toast.makeText(this, "No Location Data", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String accuracy = String.valueOf(location.getAccuracy());
//        String lat = String.valueOf(location.getLatitude());
//        String lng = String.valueOf(location.getLongitude());
//        String time = String.valueOf(location.getElapsedRealtimeNanos() / 1000000L);
//        String text = String.format("Time: %s Accuracy: %s -- lat: %s -- lng: %s", time, accuracy, lat, lng);
//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
//    }
//
//
//    private class OnReceiveLocationUpdate extends LocationCallback {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            if (locationResult != null) {
//                displaylocation(locationResult.getLastLocation());
//            } else {
//                Log.d("Location", "No Location Results");
//            }
//        }
//    }


}
