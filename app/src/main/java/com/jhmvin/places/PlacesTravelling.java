package com.jhmvin.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.util.ToastAdapter;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesTravelling extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_travelling);
        ButterKnife.bind(this);
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
        this.finish();
    }
}
