package com.jhmvin.places;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jhmvin.places.service.PlacesMainService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Android Activity Lifecycle.
 * 1. onCreate
 * 2. onStart
 * 3. onResume
 * 4. Activity Running
 * 5. onPause
 * 6. onStop - when attempting to destroy the activity.
 * 7. onDestroy
 */
public class Places extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    // Widget Variables.
    //----------------------------------------------------------------------------------------------
    @BindView(R.id.btnStartTracking)
    Button btnStartTracking;

    @BindView(R.id.btnStopTracking)
    Button btnStopTracking;

    @BindView(R.id.btnMarkLocation)
    Button btnMarkLocation;

    //----------------------------------------------------------------------------------------------
    // Activity Variables.
    //----------------------------------------------------------------------------------------------
    private ButtonClickListener buttonClickListener = new ButtonClickListener();

    //----------------------------------------------------------------------------------------------
    // 1. Create.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);
        this.addButtonClickListener();
    }

    /**
     * Add listeners to button to trigger callbacks.
     */
    private void addButtonClickListener() {
        this.btnStartTracking.setOnClickListener(this.buttonClickListener);
        this.btnStopTracking.setOnClickListener(this.buttonClickListener);
        this.btnMarkLocation.setOnClickListener(this.buttonClickListener);
    }

    private void onClickStartTracking() {
        Intent startLocationServiceIntent = new Intent(this, PlacesMainService.class);
        startLocationServiceIntent.setAction(PlacesMainService.START_LOCATION_SERVICE);
        startService(startLocationServiceIntent);
    }


    private void onClickStopTracking() {
        Intent stopLocationServiceIntent = new Intent(this, PlacesMainService.class);
        stopLocationServiceIntent.setAction(PlacesMainService.STOP_LOCATION_SERVICE);
        startService(stopLocationServiceIntent);
    }


    private void onClickMarkLocation() {
        ProgressDialog myDialog = new ProgressDialog(this);
        myDialog.setTitle("Getting Location");
        myDialog.setMessage("Please wait while getting your location.");
        myDialog.setCancelable(false);
        myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        myDialog.show();
    }


    //----------------------------------------------------------------------------------------------
    // 2. Start.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
    }

    //----------------------------------------------------------------------------------------------
    // 3. Resume.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
    }


    //----------------------------------------------------------------------------------------------
    // 5. Puase.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
    }


    //----------------------------------------------------------------------------------------------
    // 6. Stop.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
    }

    //----------------------------------------------------------------------------------------------
    // 7. Destroy.
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------------
    // Activity Methods.
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // Inner Classes.
    //----------------------------------------------------------------------------------------------

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStartTracking:
                    onClickStartTracking();
                    break;
                case R.id.btnStopTracking:
                    onClickStopTracking();
                    break;
                case R.id.btnMarkLocation:
                    onClickMarkLocation();
                    break;
            }
        }
    }

}
