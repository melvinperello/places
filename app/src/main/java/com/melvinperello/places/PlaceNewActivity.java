package com.melvinperello.places;

import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.melvinperello.places.annotation.RequiredComponent;
import com.melvinperello.places.domain.Place;
import com.melvinperello.places.feature.location.GoogleFusedLocationClient;
import com.melvinperello.places.feature.location.LocationClient;
import com.melvinperello.places.interfaces.StartingState;
import com.melvinperello.places.persistence.db.ApplicationDatabase;
import com.melvinperello.places.util.LocationTool;
import com.melvinperello.places.util.ToastAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PlaceNewActivity extends AppCompatActivity implements
        LocationClient.OnLocationObtained
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
    @RequiredComponent
    private LocationClient mLocationAwarenessClient;
    @RequiredComponent
    private PlaceNewActivityController mController;

    private Location mLocationObtained;
    private long mLocationAtomicTime = 0L;

    private boolean mFindingLocation = false;

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
        this.btnSave.setText("Waiting for location . . .");
        this.disableSaveButton();
    }

    private void disableSaveButton() {
        this.btnSave.setBackgroundColor(this.getResources().getColor(R.color.disabledBox));
        this.btnSave.setTextColor(this.getResources().getColor(R.color.disabledText));
        this.btnSave.setEnabled(false);
    }

    private void enableSaveButton() {
        this.btnSave.setBackgroundColor(this.getResources().getColor(R.color.placesGreen));
        this.btnSave.setTextColor(this.getResources().getColor(R.color.textWhite));
        this.btnSave.setEnabled(true);
    }


    private void getLocation() {
        mLocationAwarenessClient.startLocationAwareness();
        tvGeoCode.setText("Tracking Location . . .");
        this.btnSave.setEnabled(false);
        mFindingLocation = true;
    }


    private void showStringDateInUI(PlaceNewActivityController.StringDate stringDate) {
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
        if (mFindingLocation) {
            return; // do nothing
        }
        if (this.mController.checkNameIfEmpty()) {
            this.disableSaveButton();
            this.btnSave.setText("Write Something . . .");
        } else {
            // not empty
            this.enableSaveButton();
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
        place.setSource(Place.Source.LOCAL.name());
        place.setCreatedAt(mLocationAtomicTime);


        new DataPlaceInsert().execute(place);
    }


    private class DataPlaceInsert extends AsyncTask<Place, Void, Void> {

        @Override
        protected Void doInBackground(Place... places) {
            ApplicationDatabase database = ApplicationDatabase
                    .build(getApplicationContext());
            database.placeDao().insert(places[0]);
            database.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            PlaceNewActivity.this.finish();
            ToastAdapter.show(getApplicationContext(), "Successfully Added", ToastAdapter.SUCCESS);
        }
    }

    @Override
    public void onBackPressed() {
        showDiscardMessage();
        this.stopLocationRequest();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void stopLocationRequest() {
        if (mLocationAwarenessClient != null) {
            if (mLocationAwarenessClient.isLocationAware()) {
                mLocationAwarenessClient.stopLocationAwareness();
            }
        }
    }

    @Override
    public void onLocationObtained(Location location) {
        this.stopLocationRequest();
        mFindingLocation = false;
        this.mLocationObtained = location;
        this.mLocationAtomicTime = LocationTool.getLocationAtomicTime(mLocationObtained.getElapsedRealtimeNanos());

        this.onTextChangeEdtName();

        String geoCode = mController.getGeoCodeString(this.mLocationObtained);
        this.showGeoCodeInUI(geoCode);
        PlaceNewActivityController.StringDate dateString = mController.getStringDate(this.mLocationAtomicTime);
        this.showStringDateInUI(dateString);

    }
}
