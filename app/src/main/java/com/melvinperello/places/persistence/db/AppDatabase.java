package com.melvinperello.places.persistence.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {PlaceEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {


    private static AppDatabase INSTANCE;

    public static AppDatabase getFileDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room
                    .databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "places_db").build();
        }
        return INSTANCE;
    }


}
