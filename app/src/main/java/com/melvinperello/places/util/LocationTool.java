package com.melvinperello.places.util;

import android.os.SystemClock;

public class LocationTool {
    private LocationTool() {

    }

    public static long getLocationAtomicTime(long locationElapsedRealtimeNanos) {
        long bootTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime());
        long locTime = locationElapsedRealtimeNanos / 1000000;
        return bootTime + locTime;
    }
}
