package com.jhmvin.places.domain;

public interface CSVTranslation {
    void fromCSV(String csvString);

    String toCSV();
}
