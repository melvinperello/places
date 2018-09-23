package com.jhmvin.places.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * This service will handle places background process. This service will manage it's own lifecycle.
 */
public class PlacesTrackingService extends Service {
    //----------------------------------------------------------------------------------------------
    // Intent Filters.
    //----------------------------------------------------------------------------------------------
    public final static String ASK_ALIVE = PlacesTrackingService.class.getCanonicalName() + ".ASK_ALIVE";
    public final static String REPLY_ALIVE = PlacesTrackingService.class.getCanonicalName() + ".REPLY_ALIVE";
    /**
     * Instruct this service to shutdown.
     */
    public final static String ACTION_STOP_SERVICE = PlacesTrackingService.class.getCanonicalName() + ".ACTION_STOP_SERVICE";

    //----------------------------------------------------------------------------------------------
    // Service Variables.
    //----------------------------------------------------------------------------------------------
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver placesTrackingServiceReceiver;
    private LocationUpdateRequestService locationUpdateService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //----------------------------------------------------------------------------------------------
    // Create Service.
    //----------------------------------------------------------------------------------------------


    /**
     * onCreate() is called when the Service object is instantiated
     * (ie: when the service is created). You should do things in this method that you need to do
     * only once (ie: initialize some variables, etc.). onCreate() will only ever be called once
     * per instantiated object.
     * <p>
     * https://stackoverflow.com/questions/14182014/android-oncreate-or-onstartcommand-for-starting-service
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.initCreate();
        /**
         * Show created message.
         */
        Toast.makeText(this, "PlacesTrackingService: CREATED", Toast.LENGTH_SHORT).show();
    }


    private void initCreate() {
        /**
         * Get Local Broadcast Manager from SCOPE: Application Context.
         */
        this.broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        /**
         * Creates the service listener.
         */
        this.placesTrackingServiceReceiver = new PlacesTrackingServiceListener();
        /**
         * Create Location Update Service.
         */
        this.locationUpdateService = new LocationUpdateRequestService(this);
    }

    //----------------------------------------------------------------------------------------------
    // Start Service.
    //----------------------------------------------------------------------------------------------

    /**
     * onStartCommand() is called every time a client starts the service using
     * startService(Intent intent). This means that onStartCommand() can get called multiple times.
     * You should do the things in this method that are needed each time a client requests something
     * from your service. This depends a lot on what your service does and how it
     * communicates with the clients (and vice-versa).
     * <p>
     * If you don't implement onStartCommand() then you won't be able to get any information from
     * the Intent that the client passes to onStartCommand() and your service might not be able to
     * do any useful work.
     * <p>
     * https://stackoverflow.com/questions/14182014/android-oncreate-or-onstartcommand-for-starting-service
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // register broadcast listener.
        this.registerPlacesTrackingServiceListener();
        // start service process.
        this.startServiceProcess();
        // notify service started.
        Toast.makeText(this, "PlacesTrackingService: STARTED", Toast.LENGTH_SHORT).show();
        /**
         * START_STICKY means "Hey Android! If you really really have to shut down my precious Service because of running low on memory, then please please try to start it again."
         * START_NOT_STICKY would mean "Nahh...don't bother. I'll call startService() again myself if I really need my Service running."
         * This (start sticky) is probably fine most of time. Your Service will just start from scratch again. You could try if that's suitable for your use case.
         */
        return START_NOT_STICKY;
    }

    private void registerPlacesTrackingServiceListener() {
        IntentFilter actionFilters = new IntentFilter();
        actionFilters.addAction(ASK_ALIVE);
        actionFilters.addAction(ACTION_STOP_SERVICE);
        this.broadcastManager.registerReceiver(this.placesTrackingServiceReceiver, actionFilters);
    }

    private void startServiceProcess() {
        this.locationUpdateService.startLocationRequest();
    }

    //----------------------------------------------------------------------------------------------
    // Destroy Service.
    //----------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * Unregister  Broadcast Listener when destroyed.
         */
        this.unregisterPlacesTrackingServiceListener();
        //
        this.stopLocationRequests();
        /**
         * Some Destroy Message.
         */
        Toast.makeText(this, "PlacesTrackingService: DESTROYED", Toast.LENGTH_SHORT).show();
    }

    private void unregisterPlacesTrackingServiceListener() {
        this.broadcastManager.unregisterReceiver(this.placesTrackingServiceReceiver);
    }

    private void stopLocationRequests() {
        this.locationUpdateService.stopLocationRequest();
    }


    //----------------------------------------------------------------------------------------------
    // Service Methods.
    //----------------------------------------------------------------------------------------------


    /**
     * Sends a alive broadcast message with the
     * Itent Filter REPLY_ALIVE to those who will inquire to the status of this service.
     */
    private void broadcastAlive() {
        this.broadcastManager.sendBroadcast(new Intent(REPLY_ALIVE));
    }


    //----------------------------------------------------------------------------------------------
    // Inner Classes.
    //----------------------------------------------------------------------------------------------

    /**
     * This Service will listen to Context broadcast.
     */
    private class PlacesTrackingServiceListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ASK_ALIVE)) {
                broadcastAlive();
            } else if (intent.getAction().equals(ACTION_STOP_SERVICE)) {
                /**
                 * shutdown this service.
                 */
                stopSelf();
            }
        }
    }


}
