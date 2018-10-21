package com.jhmvin.places.feature.location;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LocationServiceFragment implements OnLocationObtained {


    public final static String TAG = LocationServiceFragment.class.getCanonicalName();

    /**
     * Location client that provides the location awareness feature.
     */
    private LocationClient mLocationClient;
    /**
     * Appliication Context to run this service.
     */
    private final Context mContext;
    /**
     * This is required to start location awareness.
     */
    private LocationInfoToken mLocationInfoToken;

    /**
     * @param context must be an application context that implements OnLocationUpdateMessageCreated.
     */
    public LocationServiceFragment(Context context) {
        mContext = context;
    }

    //----------------------------------------------------------------------------------------------
    // Major Operations.
    //----------------------------------------------------------------------------------------------

    /**
     * Starts the service with a location info token.
     * if the service is already started this will restart the service.
     *
     * @param locationInfoToken
     */
    public void startService(LocationInfoToken locationInfoToken) {
        // stops the service
        this.stopService();
        // start service
        mLocationClient = new GoogleFusedLocationClient(mContext);
        mLocationClient.setLocationCallback(this);
        mLocationClient.startLocationAwareness();
        Log.d(TAG, "Location service started.");
        locationInfoToken.setStarted(true);
        this.mLocationInfoToken = locationInfoToken;
    }


    /**
     * Stops the service.
     */
    public void stopService() {
        if (mLocationClient != null) {
            mLocationClient.stopLocationAwareness();
            mLocationClient.setLocationCallback(null);
            mLocationClient = null;
            Log.d(TAG, "Location Client was cleared.");
        }
        mLocationInfoToken = null;
        Log.d(TAG, "Location service was stopped..");

    }

    /**
     * Returns the location info token if available.
     *
     * @return
     */
    public LocationInfoToken getLocationInfoToken() {
        return this.mLocationInfoToken;
    }

    /**
     * When a location was obtained.
     *
     * @param location
     */
    @Override
    public void onLocationObtained(Location location) {

        LocationUpdateMessage locMessage = new LocationUpdateMessage();
        locMessage.setLongitude(location.getLongitude());
        locMessage.setLatitude(location.getLatitude());
        locMessage.setSpeed(location.getSpeed());
        locMessage.setAccuracy(location.getAccuracy());

        long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        long locTime = location.getElapsedRealtimeNanos() / 1000000;
        long timeRecordedMills = bootTime + locTime;

        locMessage.setTime(timeRecordedMills);
        // context cast to update callback.
        ((OnLocationUpdateMessageCreated) (this.mContext)).onLocationUpdateMessageCreated(locMessage);
    }

    /**
     * Sends the created location message to the implementing child.
     */
    public interface OnLocationUpdateMessageCreated {
        void onLocationUpdateMessageCreated(LocationUpdateMessage locationMessage);
    }

    /**
     * Create a list.
     *
     * @return
     */
    private List<Location> createLocationList() {
        return new ArrayList<>();
    }


}
