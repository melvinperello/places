package com.melvinperello.places.persistence.db;

public interface PlacesDatabase {

    public final static String DB_NAME = "places_db.db";

    PlaceDao placeDao();
}
