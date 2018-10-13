package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

    @BindView(R.id.tvLastTime)
    TextView tvLastTime;

    @BindView(R.id.tvLocationCount)
    TextView tvLocationCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_travelling);
        ButterKnife.bind(this);

        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_START_TRAVEL);
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

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationUpdated(PlacesTravelService.LocationUpdated locationUpdated) {
        tvLastTime.setText(locationUpdated.getLastUpdate());
    }

    // UI Updates must be on the Main Thread.
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSomething(String string) {
        ToastAdapter.show(this, "I received: " + string);
    }
}
