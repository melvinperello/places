package com.melvinperello.places.feature.tempTravel;

public interface TempCSVTranslation {
    void fromTempCSV(String csvString);

    String toTempCSV();
}
