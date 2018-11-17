package com.melvinperello.places;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.melvinperello.places.domain.Place;
import com.melvinperello.places.feature.location.GoogleFusedLocationClient;
import com.melvinperello.places.feature.location.LocationAware;
import com.melvinperello.places.interfaces.StartingState;
import com.melvinperello.places.persistence.db.ApplicationDatabase;
import com.melvinperello.places.util.LocationTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PlaceNewActivity extends AppCompatActivity implements LocationAware.OnLocationObtained
        , StartingState {

    //----------------------------------------------------------------------------------------------
    // Widgets
    //----------------------------------------------------------------------------------------------
    @BindView(R.id.tvDateDay)
    TextView tvDateDay;

    @BindView(R.id.tvDateDayName)
    TextView tvDateDayName;

    @BindView(R.id.tvDateMonthYear)
    TextView tvDateMonthYear;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.tvGeoCode)
    TextView tvGeoCode;

    @BindView(R.id.edtName)
    EditText edtName;

    @BindView(R.id.edtNotes)
    EditText edtNotes;

    @BindView(R.id.btnSave)
    Button btnSave;

    //----------------------------------------------------------------------------------------------
    // Members
    //----------------------------------------------------------------------------------------------
    private PlaceNewActivityController mController;
    private LocationAware mLocationAwarenessClient;
    private Location mLocationObtained;
    private long mLocationAtomicTime = 0L;

    //----------------------------------------------------------------------------------------------
    // Methods.
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_new);
        ButterKnife.bind(this);
        // enable back button
        getSupportActionBar().setTitle(R.string.a_new_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        this.mController = new PlaceNewActivityController();
        this.mController.setEdtName(this.edtName);
        this.mController.setEdtNotes(this.edtNotes);
        //
        mLocationAwarenessClient = new GoogleFusedLocationClient(this);
        mLocationAwarenessClient.setLocationCallback(this);
        this.getLocation();


        this.startingState();
    }

    @Override
    public void startingState() {
        this.disableSaveButtonInUI();
    }

    private void disableSaveButtonInUI() {
        this.btnSave.setText("Write Something . . .");
        this.btnSave.setEnabled(false);
    }


    private void getLocation() {
        mLocationAwarenessClient.startLocationAwareness();
        tvGeoCode.setText("Tracking Location . . .");
        this.btnSave.setEnabled(false);
    }


    public final static class StringDate {
        private String day;
        private String dayName;
        private String monthYear;
        private String time;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDayName() {
            return dayName;
        }

        public void setDayName(String dayName) {
            this.dayName = dayName;
        }

        public String getMonthYear() {
            return monthYear;
        }

        public void setMonthYear(String monthYear) {
            this.monthYear = monthYear;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }


    private void showStringDateInUI(StringDate stringDate) {
        // display time.
        tvDateDay.setText(stringDate.getDay());
        tvDateDayName.setText(stringDate.getDayName());
        tvDateMonthYear.setText(stringDate.getMonthYear());
        tvTime.setText(stringDate.getTime());
    }

    private void showDiscardMessage() {
        // if no text just finish the activity.
        if (mController.checkNameAndNoteIfEmpty()) {
            finish();
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage("Discard Changes ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .create().show();
    }


    private void showGeoCodeInUI(String geoCode) {
        tvGeoCode.setText(geoCode);
    }

    @OnTextChanged(R.id.edtName)
    public void onTextChangeEdtName() {
        if (this.mController.checkNameIfEmpty()) {
            this.disableSaveButtonInUI();
        } else {
            // not empty
            this.btnSave.setEnabled(true);
            this.btnSave.setText("Save");
        }
    }


    @OnClick(R.id.btnSave)
    public void onClickBtnSave() {

        // create entity.
        final Place place = new Place();
        place.setName(edtName.getText().toString().trim());
        place.setNote(edtNotes.getText().toString().trim());
        place.setLatitude(mLocationObtained.getLatitude());
        place.setLongitude(mLocationObtained.getLongitude());
        place.setCreatedAt(mLocationAtomicTime);


        new Thread(new Runnable() {
            @Override
            public void run() {
                ApplicationDatabase database = ApplicationDatabase
                        .build(getApplicationContext());
                database.placeDao().insert(place);
                database.close();
                PlaceNewActivity.this.finish();
            }
        }).start();


    }

    @Override
    public void onBackPressed() {
        showDiscardMessage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        showDiscardMessage();
        return true;
    }

    @Override
    public void onLocationObtained(Location location) {
        mLocationAwarenessClient.stopLocationAwareness();
        this.mLocationObtained = location;
        this.mLocationAtomicTime = LocationTool.getLocationAtomicTime(mLocationObtained.getElapsedRealtimeNanos());
        this.btnSave.setEnabled(true);

        String geoCode = mController.getGeoCodeString(this.mLocationObtained);
        this.showGeoCodeInUI(geoCode);
        StringDate dateString = mController.getStringDate(this.mLocationAtomicTime);
        this.showStringDateInUI(dateString);
    }
}
