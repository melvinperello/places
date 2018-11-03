package com.melvinperello.places.feature.location;

import android.content.Context;
import android.util.Log;

/**
 * Location Service.
 */
public class LocationService {


    public final static String TAG = LocationService.class.getCanonicalName();

    /**
     * Location client that provides the location awareness feature.
     */
    private LocationClient mLocationClient;
    /**
     * Appliication Context to run this service.
     */
    private final Context mContext;

    private final LocationClient.OnLocationObtained mCallback;

    /**
     * @param context must be an application context that implements OnLocationUpdateMessageCreated.
     */
    public LocationService(Context context, LocationClient.OnLocationObtained callback) {
        mContext = context;
        mCallback = callback;
    }

    //----------------------------------------------------------------------------------------------
    // Major Operations.
    //----------------------------------------------------------------------------------------------

    /**
     * Starts the service with a location info token.
     * if the service is already started this will restart the service.
     */
    public void startService() {
        // stops the service
        this.stopService();
        // start service
        mLocationClient = new GoogleFusedLocationClient(mContext);
        mLocationClient.setLocationCallback(mCallback);
        mLocationClient.startLocationAwareness();
        Log.d(TAG, "Location service started.");
        // create cache
        Log.d(TAG, "Location cache created.");
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
        Log.d(TAG, "Location service was stopped..");
    }


}
