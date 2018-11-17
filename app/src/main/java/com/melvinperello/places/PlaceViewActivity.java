package com.melvinperello.places;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.melvinperello.places.domain.Place;
import com.melvinperello.places.domain.PlacesListItem;
import com.melvinperello.places.persistence.db.ApplicationDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceViewActivity extends AppCompatActivity {

    @BindView(R.id.rvPlaces)
    RecyclerView rvPlaces;


    private final List<PlacesListItem> placesList = new ArrayList<>();
    private final PlacesListItem.DataAdapter placesAdapter = new PlacesListItem.DataAdapter(placesList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
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
        new DataLoader().execute("Something");
    }


    private final class DataLoader extends AsyncTask<String, Void, List<PlacesListItem>> {

        @Override
        protected List<PlacesListItem> doInBackground(String... strings) {
            ApplicationDatabase database = ApplicationDatabase
                    .build(PlaceViewActivity.this.getApplicationContext());
            List<Place> items = database.placeDao().allActive();
            database.close();

            List<PlacesListItem> displayItems = new LinkedList<>();
            for (Place place : items) {
                PlacesListItem displayItem = new PlacesListItem();
                displayItem.setName(place.getName());
                displayItem.setDate(String.valueOf(place.getCreatedAt()));
                displayItems.add(displayItem);
            }
            return displayItems;
        }

        @Override
        protected void onPostExecute(List<PlacesListItem> placesListItems) {
            placesList.clear();
            placesList.addAll(placesListItems);
            Log.i("PlaceViewActivity", "Items Received: " + placesListItems.size());
            placesAdapter.notifyDataSetChanged();
        }
    }
}
