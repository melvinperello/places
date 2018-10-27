package com.jhmvin.places.feature.tempTravel;

import com.jhmvin.places.util.StringTool;

public class TempTravelHeaderBean implements TempCSVTranslation {
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
    public void fromTempCSV(String csvString) {
        //
        String[] splitted = StringTool.splitCSV(csvString);
        String start = splitted[0];
        if (start.equals("START")) {
            this.startPlace = StringTool.unqoute(splitted[1]);
            this.endPlace = StringTool.unqoute(splitted[2]);
            this.startTime = Long.parseLong(splitted[3]);
        }
    }

    @Override
    public String toTempCSV() {
        return String.format("START,\"%s\",\"%s\",%s", this.startPlace, this.endPlace, this.startTime);
    }
}
