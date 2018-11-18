package com.melvinperello.places;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.melvinperello.places.domain.Place;
import com.melvinperello.places.domain.PlacesListItem;
import com.melvinperello.places.persistence.db.ApplicationDatabase;
import com.melvinperello.places.ui.SearchToolbarFixer;
import com.melvinperello.places.util.LocationTool;
import com.melvinperello.places.util.ToastAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceViewActivity extends AppCompatActivity implements MenuItem.OnActionExpandListener {

    @BindView(R.id.rvPlaces)
    RecyclerView rvPlaces;


    /**
     * Toolbar Instance.
     */
    private SearchToolbarFixer mSearchToolbarFixer;


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!query.isEmpty()) {
                new DataLoader().execute(DATA_NAME_LIKE, query);
            }
        }
    }


    private final List<PlacesListItem> placesList = new ArrayList<>();
    private final PlacesListItem.DataAdapter placesAdapter = new PlacesListItem.DataAdapter(placesList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_view);
        ButterKnife.bind(this);


        mSearchToolbarFixer = SearchToolbarFixer.create(this)
                .addCustomToolbar(R.id.toolbar);


        getSupportActionBar().setTitle("Your Places");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setupRecylcerView();

        this.populateFakeData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mSearchToolbarFixer
                .setMenuResource(R.menu.menu_action_search)
                .setMenu(menu)
                .inflate()
                .setCloseButtonAsClear()
                .setOnActionExpandListener(this);
        return true;
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


    public final static String DATA_ALL = "DATA_ALL";
    public final static String DATA_NAME_LIKE = "DATA_NAME_LIKE";

    private void populateFakeData() {
        new DataLoader().execute(DATA_ALL);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        populateFakeData();
        return true;
    }


    private final class DataLoader extends AsyncTask<String, Void, List<PlacesListItem>> {

        @Override
        protected List<PlacesListItem> doInBackground(String... strings) {
            String commandType = strings[0];

            ApplicationDatabase database = ApplicationDatabase
                    .build(PlaceViewActivity.this.getApplicationContext());
            // filter
            // init empty
            List<Place> items = new ArrayList<>();
            switch (commandType) {
                case DATA_ALL:
                    items = database.placeDao().allActive();
                    break;
                case DATA_NAME_LIKE:
                    String searchQuery = strings[1];
                    items = database.placeDao().findNameLike(searchQuery);
                    break;
            }
            //


            database.close();

            List<PlacesListItem> displayItems = new LinkedList<>();
            for (Place place : items) {
                PlacesListItem displayItem = new PlacesListItem();
                displayItem.setName(place.getName());
                String displayGeoCode = place.getRevGeoCode() == null ? "No Geo Code" : place.getRevGeoCode();

                displayItem.setGeoCode(displayGeoCode);
                long displayTime = place.getCreatedAt();

                if (place.getUpdatedAt() != 0) {
                    displayTime = place.getUpdatedAt();
                }

                displayItem.setDate(LocationTool.getLocationTimeInString("MMMMMMMMM dd, yyyy hh:mm a", displayTime));
                displayItems.add(displayItem);
            }
            return displayItems;
        }

        @Override
        protected void onPostExecute(List<PlacesListItem> placesListItems) {
            placesList.clear();
            placesList.addAll(placesListItems);
            placesAdapter.notifyDataSetChanged();
        }
    }
}
