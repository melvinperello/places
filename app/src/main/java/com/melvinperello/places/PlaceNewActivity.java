package com.melvinperello.places;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.melvinperello.places.feature.location.GoogleFusedLocationClient;
import com.melvinperello.places.feature.location.LocationAware;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceNewActivity extends AppCompatActivity implements LocationAware.OnLocationObtained {


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



    private ProgressDialog mProgressDialog;


    private LocationAware mLocationAwarenessClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_new);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(R.string.a_new_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTime(System.currentTimeMillis());
        mLocationAwarenessClient = new GoogleFusedLocationClient(this);
        mLocationAwarenessClient.setLocationCallback(this);
        this.getLocation();
    }



    private void getLocation() {
        mLocationAwarenessClient.startLocationAwareness();
        mProgressDialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
    }


    private void setTime(long currentTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);
        Date date = cal.getTime();
        String day = new SimpleDateFormat("dd").format(date);
        String dayName = new SimpleDateFormat("EEEE").format(date);
        String monthYear = new SimpleDateFormat("MMMM yyyy").format(date).toUpperCase();
        String time = new SimpleDateFormat("hh:mm:ss a").format(date);


        tvDateDay.setText(day);
        tvDateDayName.setText(dayName);
        tvDateMonthYear.setText(monthYear);
        tvTime.setText(time);
    }


    private boolean hasNoText() {
        String name = edtName.getText().toString().trim();
        String note = edtNotes.getText().toString().trim();

        return (name.isEmpty() && note.isEmpty());
    }

    private void askToDiscard() {
        if (hasNoText()) {
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

    @OnClick(R.id.btnSave)
    public void onClickBtnSave() {

    }

    @Override
    public void onBackPressed() {
        askToDiscard();
    }

    @Override
    public boolean onSupportNavigateUp() {
        askToDiscard();
        return true;
    }

    @Override
    public void onLocationObtained(Location location) {
        mLocationAwarenessClient.stopLocationAwareness();
        mProgressDialog.dismiss();
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        tvGeoCode.setText(lat + " , " + lng);
    }
}
