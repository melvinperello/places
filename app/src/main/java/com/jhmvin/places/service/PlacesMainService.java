package com.jhmvin.places.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jhmvin.places.PlacesNew;
import com.jhmvin.places.feature.location.LocationInfoToken;
import com.jhmvin.places.feature.location.LocationServiceFragment;
import com.jhmvin.places.feature.location.LocationUpdateMessage;
import com.jhmvin.places.persistence.text.TempTravelLocationWriter;
import com.jhmvin.places.persistence.text.TextWriter;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlacesMainService extends Service implements BedTime, LocationServiceFragment.OnLocationUpdateMessageCreated {
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
//    public final static String ACTION_GET_POINTS = CANONICAL_NAME + ".ACTION_GET_POINTS";
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
    private LocationServiceFragment mLocationServiceFragment;

    private LocationInfoToken createLocationToken(Intent intent) {
        LocationInfoToken locationToken = new LocationInfoToken();
        locationToken.setPlaceToStart(intent.getExtras().getString(PlacesNew.EXTRA_PLACE_START));
        locationToken.setPlaceToEnd(intent.getExtras().getString(PlacesNew.EXTRA_PLACE_END));
        locationToken.setTimeStarted(System.currentTimeMillis());
        locationToken.setStarted(false);
        return locationToken;
    }

    private void startLocationService(Intent intent) {
        LocationInfoToken token = createLocationToken(intent);
        if (mLocationServiceFragment == null) {
            mLocationServiceFragment = new LocationServiceFragment(PlacesMainService.this);
        }
        mLocationServiceFragment.startService(token);
        ToastAdapter.show(getApplicationContext(), "Travel Start Command", ToastAdapter.SUCCESS);
        // experimental block
        String fileName = token.getPlaceToStart() + "_" + token.getPlaceToEnd() + "_" + String.valueOf(token.getTimeStarted());
        this.createWriter(fileName); // no extension please.
        try {
            this.tempTravelLocationWriter.open();
            // write something
            String formatted = String.format("\"START\",\"%s\",\"%s\",\"%s\"", token.getPlaceToStart(), token.getPlaceToEnd(), token.getTimeStarted());
            this.tempTravelLocationWriter.write(formatted);
            // flush data
            this.tempTravelLocationWriter.flush();
        } catch (IOException e) {
            ToastAdapter.show(getApplicationContext(), "Cannot initialize writer.", ToastAdapter.ERROR);
            this.unsetWriter();
        }

    }

    private void stopLocationService() {
        if (mLocationServiceFragment != null) {
            mLocationServiceFragment.stopService();
        }
        mLocationServiceFragment = null;
        ToastAdapter.show(getApplicationContext(), "Travel Stop Command", ToastAdapter.ERROR);

        //
        this.closeWriter();
    }

    //----------------------------------------------------------------------------------------------
    // Posting Feature.
    //----------------------------------------------------------------------------------------------

    private void checkLocationService() {
        if (mLocationServiceFragment == null) {
            EventBus.getDefault().post(new LocationUpdateMessage());
        } else {
            EventBus.getDefault().post(mLocationServiceFragment.getLocationInfoToken());
        }
    }

    @Override
    public void onLocationUpdateMessageCreated(LocationUpdateMessage locationMessage) {
        EventBus.getDefault().post(locationMessage);
        // experimental
        this.writeLocation(locationMessage);
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
                    checkLocationService();
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

    private TextWriter tempTravelLocationWriter;
    private List<LocationUpdateMessage> locationCache;

    private void createWriter(String fileName) {
        this.tempTravelLocationWriter = new TempTravelLocationWriter(this, fileName);
        this.locationCache = new ArrayList<>();
    }

    private void unsetWriter() {
        if (this.tempTravelLocationWriter != null) {
            try {
                this.tempTravelLocationWriter.close();
            } catch (IOException e) {
                Log.e(TAG, "Cannot Close Error Writer: " + e.getLocalizedMessage());
            }
        }

        this.tempTravelLocationWriter = null;
        this.locationCache = null;
    }

    private void writeLocation(LocationUpdateMessage location) {
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
                    for (LocationUpdateMessage loc : locationCache) {
                        this.tempTravelLocationWriter.write("\n" + loc.toCSV());
                    }
                    this.tempTravelLocationWriter.flush();
                    ToastAdapter.show(getApplicationContext(), "Write Success", ToastAdapter.SUCCESS);
                    this.locationCache.clear();
                }
            } catch (IOException e) {
                ToastAdapter.show(getApplicationContext(), "Write Error", ToastAdapter.ERROR);
                this.unsetWriter();
            }
        }
    }

    private void closeWriter() {
        if (this.tempTravelLocationWriter != null) {
            this.writeToFile();
            try {
                this.tempTravelLocationWriter.write(String.format("\n\"END\",\"%s\"", System.currentTimeMillis()));
                this.tempTravelLocationWriter.flush();
                this.tempTravelLocationWriter.close();
                this.unsetWriter();
            } catch (IOException e) {
                ToastAdapter.show(getApplicationContext(), "Saving Error", ToastAdapter.ERROR);
                this.unsetWriter();
            }
        }
    }
}
