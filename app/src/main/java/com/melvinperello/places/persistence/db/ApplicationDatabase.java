package com.melvinperello.places.persistence.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.melvinperello.places.domain.Place;

/**
 * Abstract class for the database access.
 */
@Database(entities = {Place.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase implements PlacesDatabase {


    public static ApplicationDatabase build(Context applicationContext) {
        return Room.databaseBuilder(applicationContext,
                ApplicationDatabase.class, PlacesDatabase.DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }
}
