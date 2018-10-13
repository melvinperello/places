package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.domain.message.CheckItineraryMessage;
import com.jhmvin.places.service.PlacesTravelService;
import com.jhmvin.places.util.ToastAdapter;

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

        Intent startTravelService = new Intent(this, PlacesTravelService.class);
        startTravelService.setAction(PlacesTravelService.ACTION_CHECK_ITINERARY);
        startService(startTravelService);
    }

    @OnClick(R.id.btnNewPlace)
    public void onClickBtnNewPlace() {
        this.startActivity(new Intent(this, PlacesNew.class));
        this.finish();
    }

    @OnClick(R.id.btnViewPlaces)
    public void onClickBtnViewPlaces() {
        ToastAdapter.show(this, "View Places Was Clicked");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItineraryCheck(CheckItineraryMessage itinerary) {
        if (itinerary.getStarted() != 0) {
            Intent intent = new Intent(this, PlacesTravelling.class);
            intent.putExtra(PlacesNew.EXTRA_PLACE_ORIGIN, itinerary.getOrigin());
            intent.putExtra(PlacesNew.EXTRA_PLACE_DESTINATION, itinerary.getDestination());
            this.startActivity(intent);
            this.finish();
        }
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
