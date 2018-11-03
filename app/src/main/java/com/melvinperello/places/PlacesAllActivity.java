package com.melvinperello.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.melvinperello.places.domain.PlacesListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesAllActivity extends AppCompatActivity {

    @BindView(R.id.rvPlaces)
    RecyclerView rvPlaces;


    private final List<PlacesListItem> placesList = new ArrayList<>();
    private final PlacesListItem.DataAdapter placesAdapter = new PlacesListItem.DataAdapter(placesList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_all);
        ButterKnife.bind(this);


        getSupportActionBar().setTitle("Your Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setupRecylcerView();

        this.populateFakeData();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setupRecylcerView() {
        rvPlaces.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvPlaces.setLayoutManager(manager);
        //
        rvPlaces.setAdapter(placesAdapter);
    }

    private void populateFakeData() {
        for (int ctr = 0; ctr < 20; ctr++) {
            PlacesListItem item = new PlacesListItem();
            item.setDate("November 03, 2018 05:59 PM");
            item.setName("SM Clark");
            placesList.add(item);
        }
        // send update to adapter.
        placesAdapter.notifyDataSetChanged();
    }
}
