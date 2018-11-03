package com.melvinperello.places;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.melvinperello.places.ui.controller.LocationInfoToken;
import com.melvinperello.places.service.LocationServiceUpdateMessage;
import com.melvinperello.places.service.PlacesMainService;
import com.melvinperello.places.util.TimeTool;
import com.melvinperello.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TempTravelRunning extends AppCompatActivity {
    public final static String CANONICAL_NAME = TempTravelRunning.class.getCanonicalName();
    public final static String ACTION_START_UPDATES = CANONICAL_NAME + ".ACTION_START_UPDATES";

    @BindView(R.id.tvOrigin)
    TextView tvOrigin;

    @BindView(R.id.tvDestination)
    TextView tvDestination;

    @BindView(R.id.tvTravelStart)
    TextView tvTravelStart;

    @BindView(R.id.tvTimeElapse)
    TextView tvTimeElapse;

    @BindView(R.id.tvCount)
    TextView tvCount;

    @BindView(R.id.tvLatitude)
    TextView tvLatitude;

    @BindView(R.id.tvLongitude)
    TextView tvLongitude;

    @BindView(R.id.tvSpeed)
    TextView tvSpeed;

    @BindView(R.id.tvAccuracy)
    TextView tvAccuracy;

    @BindView(R.id.tvLastTime)
    TextView tvLastTime;

    // Elapsed time counter.
    private Handler mElapsedTimeHandler;
    private Runnable mElapsedTimeHandlerCallback;


    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_running);
        // hide action bar
        getSupportActionBar().setTitle("Places: Wandering");
        // bind
        ButterKnife.bind(this);
        // clear fields
        this.clearTextViews();

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(ACTION_START_UPDATES)) {
                Intent startTravelService = new Intent(this, PlacesMainService.class);
                startTravelService.setAction(PlacesMainService.ACTION_TRAVEL_START);
                startTravelService.putExtra(TempTravelNew.EXTRA_PLACE_START, getIntent().getExtras().getString(TempTravelNew.EXTRA_PLACE_START));
                startTravelService.putExtra(TempTravelNew.EXTRA_PLACE_END, getIntent().getExtras().getString(TempTravelNew.EXTRA_PLACE_END));
                startService(startTravelService);
            }
        }
    }

    private void clearTextViews() {
        this.tvOrigin.setText("");
        this.tvDestination.setText("");
        this.tvTravelStart.setText("");
        this.tvTimeElapse.setText("");
        this.tvCount.setText("");
        tvLongitude.setText("");
        tvLatitude.setText("");
        tvSpeed.setText("");
        tvAccuracy.setText("");
        tvLastTime.setText("");
    }

    @OnClick(R.id.btnAddMarker)
    public void onClickBtnAddPoint() {
        if (this.mLastLocation == null) {
            ToastAdapter.show(this, "Please Wait,Application Not Ready.", ToastAdapter.INFO);
        } else {
            Intent intent = new Intent(this, PlacesMarkerNew.class);
            intent.putExtra("long", String.valueOf(mLastLocation.getLongitude()));
            intent.putExtra("lat", String.valueOf(mLastLocation.getLatitude()));
            intent.putExtra("time", String.valueOf(mLocationToken.getTimeStarted()));
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnViewMarker)
    public void onClickBtnViewPoints() {
        ToastAdapter.show(this, "View was clicked.");
    }

    @OnClick(R.id.btnViewGraph)
    public void onClickBtnViewGraph() {
        ToastAdapter.show(this, "Not Yet Supported.", ToastAdapter.ERROR);
    }

    @OnClick(R.id.btnStopTravel)
    public void onClickBtnStopTravel() {
        new AlertDialog.Builder(this)
                .setMessage("Is this the end of your journey ?")
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onStopTravel();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }

    private void onStopTravel() {
        Intent startTravelService = new Intent(this, PlacesMainService.class);
        startTravelService.setAction(PlacesMainService.ACTION_TRAVEL_STOP);
        startService(startTravelService);
        // start home
        Intent intent = new Intent(this, Places.class);
        startActivity(intent);
        // end this
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
        Intent startTravelService = new Intent(this, PlacesMainService.class);
        startTravelService.setAction(PlacesMainService.ACTION_TRAVEL_CHECK);
        startService(startTravelService);


    }


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        //
        Intent startTravelService = new Intent(this, PlacesMainService.class);
        startTravelService.setAction(PlacesMainService.ACTION_SERVICE_SLEEP);
        startService(startTravelService);
        // stop elapsed time counter
        this.stopElapsedTimeUpdateHandler();
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdated(LocationServiceUpdateMessage locationReceivedMessage) {
        this.updateLocationUI(locationReceivedMessage);
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTravelCheckReceived(LocationInfoToken itinerary) {
        this.updateItineraryUI(itinerary);
    }

    private LocationServiceUpdateMessage mLastLocation;
    private LocationInfoToken mLocationToken;

    private void updateLocationUI(LocationServiceUpdateMessage locationReceivedMessage) {
        this.mLastLocation = locationReceivedMessage;

        double speed = (double) locationReceivedMessage.getSpeed();
        double accuracy = (double) locationReceivedMessage.getAccuracy();

        tvLongitude.setText(String.valueOf(locationReceivedMessage.getLongitude()));
        tvLatitude.setText(String.valueOf(locationReceivedMessage.getLatitude()));
        double speedHour = speed * 60 * 60;
        double speedKm = speedHour / 1000;
        String speedKmString = decimalFormat.format(speedKm) + " km/h";
        tvSpeed.setText(decimalFormat.format(speed) + " m/s" + " -- " + speedKmString);
        tvAccuracy.setText(decimalFormat.format(accuracy) + " Radial Meter");

        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(locationReceivedMessage.getTime());
        lastUpdate.setTimeZone(TimeZone.getDefault());

        tvLastTime.setText(new SimpleDateFormat("hh:mm:ss a").format(lastUpdate.getTime()));


        this.tvCount.setText(String.valueOf(locationReceivedMessage.getCount()) + " Location Points");
    }


    private void updateItineraryUI(LocationInfoToken itinerary) {
        this.mLocationToken = itinerary;
        tvOrigin.setText(String.valueOf(itinerary.getPlaceToStart()));
        tvDestination.setText(String.valueOf(itinerary.getPlaceToEnd()));

        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(itinerary.getTimeStarted());
        lastUpdate.setTimeZone(TimeZone.getDefault());

        tvTravelStart.setText(new SimpleDateFormat("hh:mm:ss a - yyyy.MM.dd").format(lastUpdate.getTime()));
        startElapsedTimeUpdateHandler(itinerary.getTimeStarted());
    }


    private void startElapsedTimeUpdateHandler(final long startedMills) {
        mElapsedTimeHandler = new Handler(getMainLooper());
        mElapsedTimeHandlerCallback = new Runnable() {
            @Override
            public void run() {
                tvTimeElapse.setText(TimeTool.getReadableElapsedTimeFrom(startedMills));
                mElapsedTimeHandler.postDelayed(this, 1000);
            }
        };

        mElapsedTimeHandler.postDelayed(mElapsedTimeHandlerCallback, 1000);
    }

    private void stopElapsedTimeUpdateHandler() {
        if (mElapsedTimeHandler != null) {
            mElapsedTimeHandler.removeCallbacks(mElapsedTimeHandlerCallback);
            mElapsedTimeHandlerCallback = null;
            mElapsedTimeHandler = null;
        }

    }


}
