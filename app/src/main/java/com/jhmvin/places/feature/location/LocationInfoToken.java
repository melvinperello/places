package com.jhmvin.places.feature.location;

public class LocationInfoToken {

    private String placeToStart;
    private String placeToEnd;
    private long timeStarted;
    private boolean started = false;

    public LocationInfoToken() {
        //
    }


    public String getPlaceToStart() {
        return placeToStart;
    }

    public String getPlaceToEnd() {
        return placeToEnd;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public boolean isStarted() {
        return started;
    }


    public void setPlaceToStart(String placeToStart) {
        this.placeToStart = placeToStart;
    }

    public void setPlaceToEnd(String placeToEnd) {
        this.placeToEnd = placeToEnd;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
