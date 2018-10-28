package com.jhmvin.places.domain;

public class TempTravelSummaryData {
    private String from = "";
    private String to = "";
    private String timeStart = "";
    private String timeEnd = "";
    private String timeActive = "";
    private String timeIdle = "";
    private String timeTotalTravelled = "";
    private String locationCount = "";

    //----------------------------------------------------------------------------------------------
    // Getter.
    //----------------------------------------------------------------------------------------------

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public String getTimeActive() {
        return timeActive;
    }

    public String getTimeIdle() {
        return timeIdle;
    }

    public String getTimeTotalTravelled() {
        return timeTotalTravelled;
    }

    public String getLocationCount() {
        return locationCount;
    }

    //----------------------------------------------------------------------------------------------
    // Setter.
    //----------------------------------------------------------------------------------------------


    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public void setTimeActive(String timeActive) {
        this.timeActive = timeActive;
    }

    public void setTimeIdle(String timeIdle) {
        this.timeIdle = timeIdle;
    }

    public void setTimeTotalTravelled(String timeTotalTravelled) {
        this.timeTotalTravelled = timeTotalTravelled;
    }

    public void setLocationCount(String locationCount) {
        this.locationCount = locationCount;
    }
}
