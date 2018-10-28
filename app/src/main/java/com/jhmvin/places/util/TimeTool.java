package com.jhmvin.places.util;

public class TimeTool {
    private TimeTool() {

    }

    /**
     * Returns a readable mills format.
     *
     * @param fromThisTime
     * @return
     */
    public static String getReadableElapsedTimeFrom(long fromThisTime) {
        long elapseTime = System.currentTimeMillis() - fromThisTime;
        return convertMillsToString(elapseTime);
    }

    public static String convertMillsToString(long mills) {
        String time = "";
        long seconds = mills / 1000;


        long hours = seconds / 3600;
        long minutes = (seconds / 60) % 60;
        long sec = seconds % 60;
        time = sec + " sec";
        if (minutes != 0) {
            time = minutes + " min " + time;
        }

        if (hours != 0) {
            time = hours + " hr " + time;
        }


        return time;
    }
}


