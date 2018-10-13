package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jhmvin.places.domain.message.CheckItineraryMessage;
import com.jhmvin.places.domain.message.LocationReceivedMessage;
import com.jhmvin.places.service.PlacesTravelService;
import com.jhmvin.places.util.ToastAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_START_TRAVEL);
        startTravelService.putExtra(PlacesNew.EXTRA_PLACE_ORIGIN, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_ORIGIN));
        startTravelService.putExtra(PlacesNew.EXTRA_PLACE_DESTINATION, getIntent().getExtras().getString(PlacesNew.EXTRA_PLACE_DESTINATION));
        startService(startTravelService);

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
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdated(LocationReceivedMessage locationReceivedMessage) {
        tvLongitude.setText(String.valueOf(locationReceivedMessage.getLongitude()));
        tvLatitude.setText(String.valueOf(locationReceivedMessage.getLatitude()));
        tvSpeed.setText(String.valueOf(locationReceivedMessage.getSpeed()));
        tvAccuracy.setText(String.valueOf(locationReceivedMessage.getAccuracy()));
        tvLastTime.setText(String.valueOf(locationReceivedMessage.getTime()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItineraryCheck(CheckItineraryMessage itinerary) {
        tvOrigin.setText(String.valueOf(itinerary.getOrigin()));
        tvDestination.setText(String.valueOf(itinerary.getDestination()));
        tvTravelStart.setText(String.valueOf(itinerary.getStarted()));
    }


}
