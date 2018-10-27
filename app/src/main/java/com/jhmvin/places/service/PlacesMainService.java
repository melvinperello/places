package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jhmvin.places.feature.location.LocationClient;
import com.jhmvin.places.feature.location.LocationInfoToken;
import com.jhmvin.places.feature.location.LocationServiceController;
import com.jhmvin.places.feature.tempTravel.TempTravelWriter;
import com.jhmvin.places.feature.tempTravel.TempTravelFooterBean;
import com.jhmvin.places.feature.tempTravel.TempTravelHeaderBean;
import com.jhmvin.places.feature.tempTravel.TempTravelLocationBean;
import com.jhmvin.places.persistence.text.TextWriter;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    private void startLocationService(Intent intent) {
        if (locationServiceController != null) {
            locationServiceController.stopService();
        }
        locationServiceController = new LocationServiceController(this, intent);
        locationServiceController.startService();
        // experimental block
        //
        //
        //
        //
        //
        // Save The File Name
        LocationInfoToken token = locationServiceController.getToken();
        this.fileName = String.valueOf(token.getTimeStarted());
        // create the instance once
        this.createTextWriterInstance();

        try {


            TempTravelHeaderBean header = new TempTravelHeaderBean();
            header.setStartPlace(token.getPlaceToStart());
            header.setEndPlace(token.getPlaceToEnd());
            header.setStartTime(token.getTimeStarted());
            // open
            this.tempTravelLocationWriter.open();
            // write something
            this.tempTravelLocationWriter.write(header.toTempCSV());
            this.tempTravelLocationWriter.close();
        } catch (IOException e) {
            ToastAdapter.show(getApplicationContext(), "Cannot initialize writer.", ToastAdapter.ERROR);
            // incase of erro close the writer.
            this.destroyWriter();
        }

    }

    private void stopLocationService() {
        if (locationServiceController != null) {
            locationServiceController.stopService();
            locationServiceController = null;
        }
        //
        this.closeWriter();
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
        this.writeLocation(locationBean);

        // real
        if (!isStandby()) {
            LocationServiceUpdateMessage locMessage = new LocationServiceUpdateMessage();
            locMessage.setLongitude(locationBean.getLongitude());
            locMessage.setLatitude(locationBean.getLatitude());
            locMessage.setSpeed(locationBean.getSpeed());
            locMessage.setAccuracy(locationBean.getAccuracy());
            locMessage.setTime(locationBean.getTime());

            locMessage.setCount(this.locationCompleteCache.size());

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
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastAdapter.show(getApplicationContext(), "[Service Destroyed]: " + new SimpleDateFormat("hh:mm:ss a").format(new Date()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToastAdapter.show(getApplicationContext(), "[Service Started]: " + new SimpleDateFormat("hh:mm:ss a").format(new Date()));
    }

    //----------------------------------------------------------------------------------------------
    // Temp Writing Concept Block
    //----------------------------------------------------------------------------------------------

    /**
     * Text Writer Instance.
     */
    private TextWriter tempTravelLocationWriter;
    /**
     * The File name.
     */
    private String fileName;
    /**
     * Contains temporary location.
     */
    private ArrayList<TempTravelLocationBean> locationCache;
    private List<TempTravelLocationBean> locationCompleteCache;


    private void createTextWriterInstance() {
        this.tempTravelLocationWriter = new TempTravelWriter(this, this.fileName);
        this.locationCache = new ArrayList<>();
        this.locationCompleteCache = new LinkedList<>();
    }

    private void destroyWriter() {
        if (this.tempTravelLocationWriter != null) {
            try {
                this.tempTravelLocationWriter.close();
            } catch (IOException e) {
                Log.e(TAG, "Cannot Close Error Writer: " + e.getLocalizedMessage());
            }
        }

        this.tempTravelLocationWriter = null;

    }

    private void writeLocation(TempTravelLocationBean location) {
        this.locationCompleteCache.add(location);

        if (tempTravelLocationWriter != null) {
            if (locationCache != null) {
                if (location != null) {
                    // here
                    this.locationCache.add(location);
                    if (locationCache.size() >= 3) {
                        this.writeToFile();
                    }
                }
            }
        } else {
            ToastAdapter.show(getApplicationContext(), "Writing Disabled", ToastAdapter.WARNING);
        }
    }

    private void writeToFile() {
        if (this.locationCache != null) {
            try {
                if (this.tempTravelLocationWriter != null) {
                    // open the writer.
                    this.tempTravelLocationWriter.open();

                    for (TempTravelLocationBean loc : locationCache) {
                        this.tempTravelLocationWriter.write("\n" + loc.toTempCSV());
                    }
                    // close
                    this.tempTravelLocationWriter.close();

                    // clear the list
                    this.locationCache.clear();
                    ToastAdapter.show(getApplicationContext(), "Write Success [" + String.valueOf(this.locationCache.size()) + "]", ToastAdapter.SUCCESS);
                }
            } catch (IOException e) {
                ToastAdapter.show(getApplicationContext(), "Write Error", ToastAdapter.ERROR);
                this.destroyWriter();
            }
        }
    }

    private void closeWriter() {
        if (this.tempTravelLocationWriter != null) {
            this.writeToFile();
            try {
                this.tempTravelLocationWriter.open();
                TempTravelFooterBean footer = new TempTravelFooterBean();
                footer.setEndedTime(System.currentTimeMillis());
                this.tempTravelLocationWriter.write("\n" + footer.toTempCSV());
                this.tempTravelLocationWriter.close();
                this.destroyWriter();
                this.locationCache = null;
                this.locationCompleteCache.clear();
                this.locationCompleteCache = null;
            } catch (IOException e) {
                ToastAdapter.show(getApplicationContext(), "TextWriterSaving Error", ToastAdapter.ERROR);
                this.destroyWriter();
            }
        }
    }


}
