package com.melvinperello.places;

import android.location.Location;
import android.widget.EditText;

import com.melvinperello.places.annotation.Testable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlaceNewActivityController {
    private EditText edtName;

    private EditText edtNotes;

    public void setEdtName(EditText edtName) {
        this.edtName = edtName;
    }

    public void setEdtNotes(EditText edtNotes) {
        this.edtNotes = edtNotes;
    }


    private String getName() {
        return this.edtName.getText().toString().trim();
    }

    private String getNotes() {
        return this.edtNotes.getText().toString().trim();
    }

    /**
     * Sets the time to the ui.
     * <p>
     *
     * @param currentTime time when the location was received.
     */
    @Testable
    public PlaceNewActivity.StringDate getStringDate(long currentTime) {
        // get date
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        Date date = cal.getTime();
        // format
        String day = new SimpleDateFormat("dd").format(date);
        String dayName = new SimpleDateFormat("EEEE").format(date);
        String monthYear = new SimpleDateFormat("MMMM yyyy").format(date).toUpperCase();
        String time = new SimpleDateFormat("hh:mm:ss a").format(date);
        // create object
        PlaceNewActivity.StringDate dateString = new PlaceNewActivity.StringDate();
        dateString.setDay(day);
        dateString.setDayName(dayName);
        dateString.setMonthYear(monthYear);
        dateString.setTime(time);
        return dateString;
    }

    @Testable
    public boolean checkNameAndNoteIfEmpty() {
        return (getName().isEmpty() && getNotes().isEmpty());
    }

    @Testable
    public String getGeoCodeString(Location location) {
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        return String.format("%s , %s", lat, lng);
    }

    @Testable
    public boolean checkNameIfEmpty() {
        return this.getName().isEmpty();
    }
}
