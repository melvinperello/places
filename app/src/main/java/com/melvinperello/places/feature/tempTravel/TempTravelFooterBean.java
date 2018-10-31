package com.melvinperello.places.feature.tempTravel;

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
        String[] splitted = csvString.split(",");
        String end = splitted[0];
        if (end.equals("END")) {
            this.endedTime = Long.parseLong(splitted[1]);
        }
    }

    @Override
    public String toTempCSV() {
        return String.format("END,%s", this.endedTime);
    }
}
