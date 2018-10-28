package com.jhmvin.places.feature.tempTravel;

import android.location.Location;
import android.os.SystemClock;

public class TempTravelLocationBean implements TempCSVTranslation {

    public static long getAtomicTime(long nanosTime) {
        long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        long locTime = nanosTime / 1000000;
        return bootTime + locTime;
    }

    public TempTravelLocationBean() {
        // no-args
    }

    public TempTravelLocationBean(Location location) {
        this.longitude = location.getLongitude();
        this.latitude = location.getLatitude();
        this.speed = location.getSpeed();
        this.accuracy = location.getAccuracy();
        this.time = getAtomicTime(location.getElapsedRealtimeNanos());
    }

    //----------------------------------------------------------------------------------------------
    // Members.
    //----------------------------------------------------------------------------------------------
    private double longitude;
    private double latitude;
    private float speed;
    private float accuracy;
    private long time;

    //----------------------------------------------------------------------------------------------
    // Getters.
    //----------------------------------------------------------------------------------------------
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public long getTime() {
        return time;
    }

    //----------------------------------------------------------------------------------------------
    // Setters.
    //----------------------------------------------------------------------------------------------
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public void setTime(long time) {
        this.time = time;
    }


    @Override
    public void fromTempCSV(String csvString) {
        String[] split = csvString.split(",");
        this.longitude = Double.parseDouble(split[0]);
        this.latitude = Double.parseDouble(split[1]);
        this.speed = Float.parseFloat(split[2]);
        this.accuracy = Float.parseFloat(split[3]);
        this.time = Long.parseLong(split[4]);
    }

    /**
     * 1. Longitude
     * 2. Latitude
     * 3. Speed (in meters/second)
     * 4. Accuracy (Meter Radius)
     * 5. Time (in mills)
     *
     * @return a CSV representation of this object.
     */
    public String toTempCSV() {
        return String.format("%s,%s,%s,%s,%s", this.longitude, this.latitude, this.speed, this.accuracy, this.time);
    }
}
