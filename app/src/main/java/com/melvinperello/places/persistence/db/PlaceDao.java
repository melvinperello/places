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

    @Query("SELECT * FROM place WHERE deleted_at = 0")
    List<Place> allActive();

    @Query("SELECT * FROM place WHERE name LIKE '%' || :name  || '%'")
    List<Place> findNameLike(String name);

    @Query("SELECT * FROM place WHERE id = :id")
    Place findById(int id);

    @Update
    void update(Place palce);

    @Delete
    void delete(Place place);
}
