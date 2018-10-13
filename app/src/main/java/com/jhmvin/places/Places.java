package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jhmvin.places.util.ToastAdapter;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Places extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnNewPlace)
    public void onClickBtnNewPlace() {
        this.startActivity(new Intent(this, PlacesNew.class));
    }

    @OnClick(R.id.btnViewPlaces)
    public void onClickBtnViewPlaces() {
        ToastAdapter.show(this, "View Places Was Clicked");
    }
}
