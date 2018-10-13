package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jhmvin.places.domain.message.CheckItineraryMessage;
import com.jhmvin.places.domain.message.LocationReceivedMessage;
import com.jhmvin.places.service.PlacesTravelService;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesTravelling extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_travelling);
        ButterKnife.bind(this);
        this.clearTextViews();

        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_START_TRAVEL);
        startTravelService.putExtra(PlacesNew.EXTRA_PLACE_ORIGIN, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_ORIGIN));
        startTravelService.putExtra(PlacesNew.EXTRA_PLACE_DESTINATION, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_DESTINATION));
        startService(startTravelService);

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

    @OnClick(R.id.btnStopTravel)
    public void onClickBtnStopTravel() {
        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_STOP_TRAVEL);
        startService(startTravelService);
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_CHECK_ITINERARY);
        startService(startTravelService);
    }


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        someHandler.removeCallbacks(someHandlerCallback);
        someHandlerCallback = null;
        someHandler = null;
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdated(LocationReceivedMessage locationReceivedMessage) {
        tvLongitude.setText(String.valueOf(locationReceivedMessage.getLongitude()));
        tvLatitude.setText(String.valueOf(locationReceivedMessage.getLatitude()));
        tvSpeed.setText(String.valueOf(locationReceivedMessage.getSpeed()));
        tvAccuracy.setText(String.valueOf(locationReceivedMessage.getAccuracy()));

        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(locationReceivedMessage.getTime());
        lastUpdate.setTimeZone(TimeZone.getDefault());

        tvLastTime.setText(new SimpleDateFormat("hh:mm:ss a").format(lastUpdate.getTime()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItineraryCheck(CheckItineraryMessage itinerary) {
        tvOrigin.setText(String.valueOf(itinerary.getOrigin()));
        tvDestination.setText(String.valueOf(itinerary.getDestination()));

        Calendar lastUpdate = Calendar.getInstance();
        lastUpdate.setTimeInMillis(itinerary.getStarted());
        lastUpdate.setTimeZone(TimeZone.getDefault());

        tvTravelStart.setText(new SimpleDateFormat("hh:mm:ss a").format(lastUpdate.getTime()));
        updateTimeElapseTextView(itinerary.getStarted());
    }

    private Handler someHandler;
    private Runnable someHandlerCallback;

    private void updateTimeElapseTextView(final long startedMills) {
        someHandler = new Handler(getMainLooper());
        someHandlerCallback = new Runnable() {
            @Override
            public void run() {
                long elapseTime = System.currentTimeMillis() - startedMills;
                long seconds = elapseTime / 1000;

                long hours = seconds / 3600;
                long minutes = (seconds / 60) % 60;
                long sec = seconds % 60;
                String time = sec + " sec";
                if (minutes != 0) {
                    time = minutes + " min " + time;
                }

                if (hours != 0) {
                    time = hours + " hr " + time;
                }

                tvTimeElapse.setText(time);
                someHandler.postDelayed(this, 1000);
            }
        };

        someHandler.postDelayed(someHandlerCallback, 1000);


    }


}
