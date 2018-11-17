package com.melvinperello.places.persistence.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.melvinperello.places.domain.Place;

import java.util.List;

@Dao
public interface PlaceDao {
    @Insert
    void insert(Place place);

    @Query("SELECT * FROM Place WHERE deleted_at = 0")
    List<Place> allActive();

    @Update
    void update(Place palce);

    @Delete
    void delete(Place place);
}
