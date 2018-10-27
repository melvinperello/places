package com.jhmvin.places.domain.bean;

public class TempTravelFooterBean implements CSVTranslation {

    private long endedTime;

    public long getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(long endedTime) {
        this.endedTime = endedTime;
    }

    @Override
    public void fromCSV(String csvString) {

    }

    @Override
    public String toCSV() {
        return String.format("END,%s", this.endedTime);
    }
}
