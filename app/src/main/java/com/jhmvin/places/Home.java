package com.jhmvin.places;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.service.PlacesMainService;
import com.sdsmdg.tastytoast.TastyToast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Home extends AppCompatActivity {

    private PlacesMainService mService;
    private boolean mBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlacesMainService.PlacesMainServiceBinder binder = (PlacesMainService.PlacesMainServiceBinder) service;
            mService = binder.getService();
            mBound = true;
            TastyToast.makeText(getApplicationContext(), "Service Connected", TastyToast.LENGTH_LONG, TastyToast.INFO);


//            skip();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
            TastyToast.makeText(getApplicationContext(), "Service Disconnected", TastyToast.LENGTH_LONG, TastyToast.CONFUSING);
        }


    };


    private void skip(){
        if (mService.isRequestingLocations()) {
            Intent startTrackingIntent = new Intent(this, StartTracking.class);
            startActivity(startTrackingIntent);
            this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        /**
         * Hide Action Bar.
         */
        this.getSupportActionBar().hide();
    }

    @OnClick(R.id.btnStartApp)
    public void onStartApp() {
        Intent startPlacesMainService = new Intent(this, PlacesMainService.class);
        this.startService(startPlacesMainService);

        Intent startTrackingIntent = new Intent(this, StartTracking.class);
        startActivity(startTrackingIntent);
        this.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent startPlacesMainService = new Intent(this, PlacesMainService.class);
        this.startService(startPlacesMainService);
        this.bindService(startPlacesMainService, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBound = false;
    }
}
