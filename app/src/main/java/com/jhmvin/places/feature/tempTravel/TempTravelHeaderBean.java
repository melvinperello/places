package com.jhmvin.places.domain.bean;

public class TempTravelHeaderBean implements CSVTranslation {
    //----------------------------------------------------------------------------------------------
    // Members.
    //----------------------------------------------------------------------------------------------
    private String startPlace;
    private String endPlace;
    private long startTime;


    //----------------------------------------------------------------------------------------------
    // Getters.
    //----------------------------------------------------------------------------------------------
    public String getStartPlace() {
        return startPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public long getStartTime() {
        return startTime;
    }


    //----------------------------------------------------------------------------------------------
    // Setter.
    //----------------------------------------------------------------------------------------------
    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public void fromCSV(String csvString) {

    }

    @Override
    public String toCSV() {
        return String.format("\"START\",\"%s\",\"%s\",\"%s\"", this.startPlace, this.endPlace, this.startTime);
    }
}
