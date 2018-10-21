package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.domain.message.ActionTravelCheckMessage;
import com.jhmvin.places.service.PlacesMainService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Places extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);

        Intent startTravelService = new Intent(this, PlacesMainService.class);
        startTravelService.setAction(PlacesMainService.ACTION_TRAVEL_CHECK);
        startService(startTravelService);
    }

    @OnClick(R.id.btnNewPlace)
    public void onClickBtnNewPlace() {
        this.startActivity(new Intent(this, PlacesNew.class));
        this.finish();
    }

    @OnClick(R.id.btnViewPlaces)
    public void onClickBtnViewPlaces() {
        Intent intent = new Intent(this, PlacesView.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItineraryCheck(ActionTravelCheckMessage itinerary) {
        if (itinerary.isStarted()) {
            this.proceedToPlacesTravelling();
        }
    }

    private void proceedToPlacesTravelling() {
        Intent intent = new Intent(this, PlacesTravelling.class);
        this.startActivity(intent);
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
}
