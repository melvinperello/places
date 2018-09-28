package com.jhmvin.places.feature.location;

import android.content.Context;

import com.jhmvin.places.feature.Serviceable;

/**
 * Foundation class for location services.
 */
public abstract class AbstractLocationInquirer implements Serviceable {
    /**
     * What to do when there is a location upted.
     */
    protected OnLocationUpdated onLocationUpdated;
    /**
     * Composition context.
     */
    protected Context context;

    public AbstractLocationInquirer(Context context) {
        this.context = context;
    }


    /**
     * set location update callback.
     *
     * @param onLocationUpdated
     */
    public void setOnLocationUpdated(OnLocationUpdated onLocationUpdated) {
        this.onLocationUpdated = onLocationUpdated;
    }

    /**
     * local callback interface.
     */
    public interface OnLocationUpdated {

        void onLocationUpdated(android.location.Location location);
    }
}
