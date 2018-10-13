package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.jhmvin.places.util.ToastAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesNew extends AppCompatActivity {


    @BindView(R.id.edtCurrentPlace)
    EditText edtCurrentPlace;

    @BindView(R.id.edtDestinationPlace)
    EditText edtDestinationPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_new);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStartJourney)
    public void onClickBtnStartJourney() {
        String currentPlace = this.edtCurrentPlace.getText().toString().trim();
        String destinationPlace = this.edtDestinationPlace.getText().toString().trim();

        if (currentPlace.isEmpty()) {
            ToastAdapter.show(this, "Please enter your current place.", ToastAdapter.WARNING);
            return;
        }

        if (destinationPlace.isEmpty()) {
            ToastAdapter.show(this, "Please enter your destination place.", ToastAdapter.WARNING);
            return;
        }

        String displayString = String.format("Your Travelling from [%s] to [%s].", currentPlace, destinationPlace);

        this.startActivity(new Intent(this, PlacesTravelling.class));
    }

    @OnClick(R.id.btnCancelTravel)
    public void onClickBtnCancelTravel() {
        this.finish();
    }
}
