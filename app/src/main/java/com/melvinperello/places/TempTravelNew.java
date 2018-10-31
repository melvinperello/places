package com.melvinperello.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.melvinperello.places.util.ToastAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TempTravelNew extends AppCompatActivity {

    public static final String EXTRA_PLACE_START = "EXTRA_PLACE_START";
    public static final String EXTRA_PLACE_END = "EXTRA_PLACE_END";


    @BindView(R.id.edtCurrentPlace)
    EditText edtCurrentPlace;

    @BindView(R.id.edtDestinationPlace)
    EditText edtDestinationPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_travel_new);
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

        Intent intent = new Intent(this, TempTravelRunning.class);
        intent.setAction(TempTravelRunning.ACTION_START_UPDATES);
        intent.putExtra(EXTRA_PLACE_START, currentPlace);
        intent.putExtra(EXTRA_PLACE_END, destinationPlace);
        this.startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.btnCancelTravel)
    public void onClickBtnCancelTravel() {
        this.finish();
    }
}
