package com.melvinperello.places.persistence.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface PlaceDao {
    @Insert
    void insert(Place place);
}
