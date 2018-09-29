package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.jhmvin.places.service.PlacesMainService;
import com.sdsmdg.tastytoast.TastyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartTracking extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    // Widget Variables.
    //----------------------------------------------------------------------------------------------
    @BindView(R.id.edtStartLat)
    EditText edtStartLat;

    @BindView(R.id.edtStartLong)
    EditText edtStartLong;

    @BindView(R.id.edtStartLocName)
    EditText edtStartLocName;


    //----------------------------------------------------------------------------------------------
    // 1. Create.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStart)
    public void onClickButtonStart() {
        Intent startPlacesMainService = new Intent(this, PlacesMainService.class);
        startPlacesMainService.setAction(PlacesMainService.START_LOCATION_SERVICE);
        startService(startPlacesMainService);
        TastyToast.makeText(getApplicationContext(), "Welcome to places.", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
    }


}
