package com.melvinperello.places.feature.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class LocationService implements LocationClient.OnLocationObtained {


    public final static String TAG = LocationService.class.getCanonicalName();

    /**
     * Location client that provides the location awareness feature.
     */
    private LocationClient mLocationClient;
    /**
     * Appliication Context to run this service.
     */
    private final Context mContext;

    /**
     * @param context must be an application context that implements OnLocationUpdateMessageCreated.
     */
    public LocationService(Context context) {
        mContext = context;
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
        mLocationClient.setLocationCallback(this);
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


    /**
     * When a location was obtained.
     *
     * @param location
     */
    @Override
    public void onLocationObtained(Location location) {
        // send the call back chain to the service.
        ((LocationClient.OnLocationObtained) (this.mContext)).onLocationObtained(location);
    }


}
