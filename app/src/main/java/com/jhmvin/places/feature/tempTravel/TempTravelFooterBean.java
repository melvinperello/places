package com.jhmvin.places.feature.tempTravel;

public class TempTravelFooterBean implements TempCSVTranslation {

    private long endedTime;

    public long getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(long endedTime) {
        this.endedTime = endedTime;
    }

    @Override
    public void fromTempCSV(String csvString) {

    }

    @Override
    public String toTempCSV() {
        return String.format("END,%s", this.endedTime);
    }
}
