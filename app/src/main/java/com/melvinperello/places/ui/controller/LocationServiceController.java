package com.melvinperello.places.ui.controller;

import android.content.Context;
import android.content.Intent;

import com.melvinperello.places.TempTravelNew;
import com.melvinperello.places.feature.location.LocationClient;
import com.melvinperello.places.feature.location.LocationService;
import com.melvinperello.places.util.ToastAdapter;

/**
 * Wires up the location service to the main service.
 */
public class LocationServiceController {
    private Intent mStartLocationIntent;
    private LocationService mLocationService;
    private Context mServiceContext;
    private LocationInfoToken mToken;


    /**
     * @param context              requires the service context.
     * @param startLocaationIntent and the start location intent.
     */
    public LocationServiceController(Context context, Intent startLocaationIntent) {
        this.mStartLocationIntent = startLocaationIntent;
        this.mServiceContext = context;
    }

    /**
     * Create a location start token using the start intent.
     *
     * @return
     */
    private LocationInfoToken createLocationToken() {
        LocationInfoToken locationToken = new LocationInfoToken();
        locationToken.setPlaceToStart(mStartLocationIntent.getExtras().getString(TempTravelNew.EXTRA_PLACE_START));
        locationToken.setPlaceToEnd(mStartLocationIntent.getExtras().getString(TempTravelNew.EXTRA_PLACE_END));
        locationToken.setTimeStarted(System.currentTimeMillis());
        locationToken.setStarted(false);
        return locationToken;
    }


    /**
     * Start the service.
     */
    public void startService() {
        this.mToken = createLocationToken();

        if (mLocationService == null) {
            mLocationService = new LocationService(mServiceContext, (LocationClient.OnLocationObtained) mServiceContext);
        }
        mLocationService.startService();
        mToken.setStarted(true);
        ToastAdapter.show(mServiceContext.getApplicationContext(), "Travel Start Command", ToastAdapter.SUCCESS);
    }

    /**
     * get the created token.
     *
     * @return
     */
    public LocationInfoToken getToken() {
        return mToken;
    }

    /**
     * Stops the service.
     */
    public void stopService() {
        if (mLocationService != null) {
            mLocationService.stopService();
        }
        mToken = null;
        mLocationService = null;
        ToastAdapter.show(mServiceContext.getApplicationContext(), "Travel Stop Command", ToastAdapter.ERROR);
    }


}
