package com.melvinperello.places.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.melvinperello.places.R;
import com.melvinperello.places.feature.location.GoogleFusedLocationClient;
import com.melvinperello.places.feature.location.LocationClient;
import com.melvinperello.places.ui.controller.LocationInfoToken;
import com.melvinperello.places.ui.controller.LocationServiceController;
import com.melvinperello.places.ui.notification.NotificationService;
import com.melvinperello.places.feature.tempTravel.TempTravelCacheService;
import com.melvinperello.places.feature.tempTravel.TempTravelFooterBean;
import com.melvinperello.places.feature.tempTravel.TempTravelHeaderBean;
import com.melvinperello.places.feature.tempTravel.TempTravelLocationBean;
import com.melvinperello.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlacesMainService extends Service implements Standby, LocationClient.OnLocationObtained {
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

    public final static String ACTION_SERVICE_SLEEP = CANONICAL_NAME + ".ACTION_SERVICE_SLEEP";
    /**
     * Intent Action to get all recorded location in this session.
     */
//    public final static String ACTION_GET_POINTS = CANONICAL_NAME + ".ACTION_GET_POINTS";
    //----------------------------------------------------------------------------------------------
    // Service Awake (Standby).
    //----------------------------------------------------------------------------------------------
    /**
     * Is this service Awake ?
     */
    private boolean mStandbyEnabled = true;

    @Override
    public void setStanbyEnabled(boolean standby) {
        this.mStandbyEnabled = standby;
    }

    @Override
    public boolean isStandby() {
        return this.mStandbyEnabled;
    }


    //----------------------------------------------------------------------------------------------
    // Creation.
    //----------------------------------------------------------------------------------------------

    private NotificationService notificationService;

    @Override
    public void onCreate() {
        super.onCreate();
        // create notification service.
        notificationService = new NotificationService(this);
    }

    //----------------------------------------------------------------------------------------------
    // Binding Section.
    //----------------------------------------------------------------------------------------------

    /**
     * Local Binder.
     */
    public class MainServiceBinder extends Binder {
        public PlacesMainService getService() {
            return PlacesMainService.this;
        }
    }

    private final IBinder mLocalBinder = new MainServiceBinder();

    /**
     * This service is not bound. Ignore this.
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    //----------------------------------------------------------------------------------------------
    // Service.
    //----------------------------------------------------------------------------------------------
    /**
     * Controls the location service.
     */
    private LocationServiceController locationServiceController;
    /**
     * Location Cache.
     */
    private TempTravelCacheService mTempTravelCache;


    private void startLocationService(Intent intent) {
        // create notification service.
        Notification foregroundNotification = this.notificationService.createForegroundNotification();
        // show on foreground.
        this.notificationService
                .getNotificationManager()
                .notify(NotificationService.ID_FOREGROUND, foregroundNotification);
        // put service on foreground.
        startForeground(NotificationService.ID_FOREGROUND, foregroundNotification);


        if (locationServiceController != null) {
            locationServiceController.stopService();
        }
        locationServiceController = new LocationServiceController(this, intent);
        //
        LocationClient locationAware = new GoogleFusedLocationClient(this);
        locationAware.setLocationCallback(this);
        locationServiceController.setLocationAwarenessClient(locationAware);
        //
        locationServiceController.startService();
        // cache
        // get token
        LocationInfoToken token = locationServiceController.getToken();
        // create instance
        mTempTravelCache = new TempTravelCacheService(this, String.valueOf(token.getTimeStarted()));
        // create header from token.
        TempTravelHeaderBean header = new TempTravelHeaderBean();
        header.setStartPlace(token.getPlaceToStart());
        header.setEndPlace(token.getPlaceToEnd());
        header.setStartTime(token.getTimeStarted());
        mTempTravelCache.write(header.toTempCSV());
    }

    private void stopLocationService() {
        if (locationServiceController != null) {
            locationServiceController.stopService();
            locationServiceController = null;
        }
        //
        if (mTempTravelCache != null) {
            mTempTravelCache.persistCache();
            TempTravelFooterBean footer = new TempTravelFooterBean();
            footer.setEndedTime(System.currentTimeMillis());
            mTempTravelCache.write("\n" + footer.toTempCSV());
            mTempTravelCache.stopService(); // release all resources
            mTempTravelCache = null;
        }

        // remove service from foreground
        stopForeground(true);

    }

    //----------------------------------------------------------------------------------------------
    // Posting Feature.
    //----------------------------------------------------------------------------------------------

    private void checkLocationService() {
        if (this.locationServiceController == null) {
            EventBus.getDefault().post(new LocationServiceUpdateMessage());
        } else {
            EventBus.getDefault().post(locationServiceController.getToken());
        }
    }

    @Override
    public void onLocationObtained(Location location) {
        // experimental
        TempTravelLocationBean locationBean = new TempTravelLocationBean(location);
        mTempTravelCache.cache(locationBean);

        // real
        if (!isStandby()) {
            LocationServiceUpdateMessage locMessage = new LocationServiceUpdateMessage();
            locMessage.setLongitude(locationBean.getLongitude());
            locMessage.setLatitude(locationBean.getLatitude());
            locMessage.setSpeed(locationBean.getSpeed());
            locMessage.setAccuracy(locationBean.getAccuracy());
            locMessage.setTime(locationBean.getTime());

            locMessage.setCount(mTempTravelCache.getLocationCount());

            EventBus.getDefault().post(locMessage);
        } else {
            Log.d(TAG, "Sleep mode -> no location broadcast");
        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(ACTION_TRAVEL_START)) {
                    startLocationService(intent);
                } else if (intent.getAction().equals(ACTION_TRAVEL_STOP)) {
                    stopLocationService();
                } else if (intent.getAction().equals(ACTION_TRAVEL_CHECK)) {
                    setStanbyEnabled(false);
                    checkLocationService();
                } else if (intent.getAction().equals(ACTION_SERVICE_SLEEP)) {
                    setStanbyEnabled(true);
                }
            }
        } else {
            // null intent
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Service Recreated")
                    .setContentText("The service was killed.")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, mBuilder.build());
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastAdapter.show(getApplicationContext(), "[Service Destroyed]: " + new SimpleDateFormat("hh:mm:ss a").format(new Date()));
    }


}
