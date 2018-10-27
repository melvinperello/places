package com.jhmvin.places.service;

/**
 * A Message to broadcast the data from the location service.
 */
public class LocationServiceUpdateMessage {

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


    /**
     * CSV Format.
     *
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", this.longitude, this.latitude, this.speed, this.accuracy, this.time);
    }

    public String toCSV() {
        return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"", this.longitude, this.latitude, this.speed, this.accuracy, this.time);
    }
}
