package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jhmvin.places.feature.location.LocationInfoToken;
import com.jhmvin.places.feature.location.LocationUpdateMessage;
import com.jhmvin.places.service.PlacesMainService;
import com.jhmvin.places.util.ToastAdapter;

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

public class PlacesTravelling extends AppCompatActivity {
    public final static String CANONICAL_NAME = PlacesTravelling.class.getCanonicalName();
    public final static String ACTION_START_UPDATES = CANONICAL_NAME + ".ACTION_START_UPDATES";

    @BindView(R.id.tvOrigin)
    TextView tvOrigin;

    @BindView(R.id.tvDestination)
    TextView tvDestination;

    @BindView(R.id.tvTravelStart)
    TextView tvTravelStart;

    @BindView(R.id.tvTimeElapse)
    TextView tvTimeElapse;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_travelling);
        // hide action bar
        getSupportActionBar().hide();
        // bind
        ButterKnife.bind(this);
        // clear fields
        this.clearTextViews();

        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equals(ACTION_START_UPDATES)) {
                Intent startTravelService = new Intent(this, PlacesMainService.class);
                startTravelService.setAction(PlacesMainService.ACTION_TRAVEL_START);
                startTravelService.putExtra(PlacesNew.EXTRA_PLACE_START, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_START));
                startTravelService.putExtra(PlacesNew.EXTRA_PLACE_END, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_END));
                startService(startTravelService);
            }
        }
    }

    private void clearTextViews() {
        this.tvOrigin.setText("");
        this.tvDestination.setText("");
        this.tvTravelStart.setText("");
        this.tvTimeElapse.setText("");
        tvLongitude.setText("");
        tvLatitude.setText("");
        tvSpeed.setText("");
        tvAccuracy.setText("");
        tvLastTime.setText("");
    }

    @OnClick(R.id.btnAddPoint)
    public void onClickBtnAddPoint() {
        ToastAdapter.show(this, "Add was clicked.");
    }

    @OnClick(R.id.btnViewPoints)
    public void onClickBtnViewPoints() {
        ToastAdapter.show(this, "View was clicked.");
    }

    @OnClick(R.id.btnViewGraph)
    public void onClickBtnViewGraph() {
        ToastAdapter.show(this, "Not Yet Supported.", ToastAdapter.ERROR);
    }

    @OnClick(R.id.btnStopTravel)
    public void onClickBtnStopTravel() {
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
        // stop elapsed time counter
        this.stopElapsedTimeUpdateHandler();
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdated(LocationUpdateMessage locationReceivedMessage) {
        this.updateLocationUI(locationReceivedMessage);
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTravelCheckReceived(LocationInfoToken itinerary) {
        this.updateItineraryUI(itinerary);
    }

    private void updateLocationUI(LocationUpdateMessage locationReceivedMessage) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
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
    }


    private void updateItineraryUI(LocationInfoToken itinerary) {
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
                tvTimeElapse.setText(getElapsedTimeString(startedMills));
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

    private String getElapsedTimeString(long startedMills) {
        String time = "";
        long elapseTime = System.currentTimeMillis() - startedMills;
        long seconds = elapseTime / 1000;

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
