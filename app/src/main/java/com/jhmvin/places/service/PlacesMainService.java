package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jhmvin.places.feature.location.LocationClient;
import com.jhmvin.places.feature.location.LocationInfoToken;
import com.jhmvin.places.feature.location.LocationServiceController;
import com.jhmvin.places.persistence.text.TempTravelLocationWriter;
import com.jhmvin.places.persistence.text.TextWriter;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    // Binding Section.
    //----------------------------------------------------------------------------------------------

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
        this.fileName = token.getPlaceToStart() + "_" + token.getPlaceToEnd() + "_" + String.valueOf(token.getTimeStarted());
        // create the instance once
        this.createTextWriterInstance();

        try {
            // open
            this.tempTravelLocationWriter.open();
            // write something
            String formatted = String.format("\"START\",\"%s\",\"%s\",\"%s\"", token.getPlaceToStart(), token.getPlaceToEnd(), token.getTimeStarted());
            this.tempTravelLocationWriter.write(formatted);
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
        LocationServiceUpdateMessage locMessage = new LocationServiceUpdateMessage();
        locMessage.setLongitude(location.getLongitude());
        locMessage.setLatitude(location.getLatitude());
        locMessage.setSpeed(location.getSpeed());
        locMessage.setAccuracy(location.getAccuracy());

        long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        long locTime = location.getElapsedRealtimeNanos() / 1000000;
        long timeRecordedMills = bootTime + locTime;

        locMessage.setTime(timeRecordedMills);
        if (!isStandby()) {
            EventBus.getDefault().post(locMessage);
        } else {
            Log.d(TAG, "Sleep mode -> no location broadcast");
        }
        // experimental
        this.writeLocation(locMessage);
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
    private ArrayList<LocationServiceUpdateMessage> locationCache;


    private void createTextWriterInstance() {
        this.tempTravelLocationWriter = new TempTravelLocationWriter(this, this.fileName);
        this.locationCache = new ArrayList<>();
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

    private void writeLocation(LocationServiceUpdateMessage location) {
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

                    for (LocationServiceUpdateMessage loc : locationCache) {
                        this.tempTravelLocationWriter.write("\n" + loc.toCSV());
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
                this.tempTravelLocationWriter.write(String.format("\n\"END\",\"%s\"", System.currentTimeMillis()));
                this.tempTravelLocationWriter.close();
                this.destroyWriter();
                this.locationCache = null;
            } catch (IOException e) {
                ToastAdapter.show(getApplicationContext(), "Saving Error", ToastAdapter.ERROR);
                this.destroyWriter();
            }
        }
    }


}
