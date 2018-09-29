package com.jhmvin.places.persistence;

public class LocationPOJO {
    private float accuracy = 0.0f;
    private long time = 0;
    private long elapsedRealtimeNanos = 0;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private double altitude = 0.0f;
    private float speed = 0.0f;
    private float bearing = 0.0f;

    //----------------------------------------------------------------------------------------------
    // Fill.
    //----------------------------------------------------------------------------------------------

    public LocationPOJO() {

    }

    public LocationPOJO(android.location.Location l) {
        this.accuracy = l.getAccuracy();
        this.time = l.getTime();
        this.elapsedRealtimeNanos = l.getElapsedRealtimeNanos();
        this.latitude = l.getLatitude();
        this.longitude = l.getLongitude();
        this.altitude = l.getAltitude();
        this.speed = l.getSpeed();
        this.bearing = l.getBearing();
    }


    //----------------------------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------------------------

    public long getTime() {
        return time;
    }

    public long getElapsedRealtimeNanos() {
        return elapsedRealtimeNanos;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public float getBearing() {
        return bearing;
    }


    //----------------------------------------------------------------------------------------------
    // Setters
    //----------------------------------------------------------------------------------------------


    public void setTime(long time) {
        this.time = time;
    }

    public void setElapsedRealtimeNanos(long elapsedRealtimeNanos) {
        this.elapsedRealtimeNanos = elapsedRealtimeNanos;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

}
