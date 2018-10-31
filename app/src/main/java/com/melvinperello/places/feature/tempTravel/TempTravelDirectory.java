package com.melvinperello.places.feature.tempTravel;

import android.content.Context;

import java.io.File;

/**
 * Utility Class to get the temp travel directory.
 */
public class TempTravelDirectory {
    public final static String DIR_TEMP_TRAVEL_STREAMS = "temp_travel_locations";

    public static File getWorkingDirectory(Context context) {
        return context.getDir(DIR_TEMP_TRAVEL_STREAMS, Context.MODE_PRIVATE);
    }

    public static File getFile(Context context, String fileName) {
        for (File file : getWorkingDirectory(context).listFiles()) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    private TempTravelDirectory() {
    }
}
