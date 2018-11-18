package com.melvinperello.places.util;

import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocationTool {
    private LocationTool() {

    }

    /**
     * Get the location atomic time based on boot time.
     *
     * @param locationElapsedRealtimeNanos
     * @return
     */
    public static long getLocationAtomicTime(long locationElapsedRealtimeNanos) {
        long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        long locTime = locationElapsedRealtimeNanos / 1000000;
        return bootTime + locTime;
    }


    public static String getLocationTimeInString(String format, long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return new SimpleDateFormat(format).format(calendar.getTime());
    }
}
