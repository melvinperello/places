package com.melvinperello.places;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.melvinperello.places.ui.CustomSupportToolbarActivity;
import com.melvinperello.places.ui.HomeFabSpeedDial;
import com.melvinperello.places.ui.HomeNavDrawer;
import com.melvinperello.places.util.ToastAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements CustomSupportToolbarActivity {

    public final static String TAG = HomeActivity.class.getCanonicalName();


    @Override
    public Toolbar getCustomSupportToolbar() {
        return mCustomSupportToolbar;
    }

    /**
     * The drawer instance.
     */
    private Drawer mHomeDrawer;
    /**
     * Toolbar Instance.
     */
    private Toolbar mCustomSupportToolbar;


    @BindView(R.id.fabSpeedDial)
    com.leinardi.android.speeddial.SpeedDialView fabSpeedDial;

    /**
     * Loads the custom toolbar.
     */
    private void makeCustomToolbar() {
        // create custom toolbar
        this.mCustomSupportToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.mCustomSupportToolbar);
    }


    private final Drawer.OnDrawerItemClickListener mOnDrawerClick = new OnDrawerClick();

    private class OnDrawerClick implements Drawer.OnDrawerItemClickListener {

        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            switch ((int) drawerItem.getIdentifier()) {
                case HomeNavDrawer.ITEM_PLACE:
                    mHomeDrawer.closeDrawer();
                    //
                    Intent places = new Intent(HomeActivity.this, PlaceViewActivity.class);
                    startActivity(places);
                    break;

                case HomeNavDrawer.ITEM_SETTINGS:
                    mHomeDrawer.closeDrawer();
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("Settings")
                            .setPositiveButton("OK", null)
                            .create().show();
                    break;
                case HomeNavDrawer.ITEM_ABOUT:
                    mHomeDrawer.closeDrawer();
                    Intent about = new Intent(HomeActivity.this, AboutActivity.class);
                    startActivity(about);

                    break;
                case HomeNavDrawer.ITEM_EXIT:
                    mHomeDrawer.closeDrawer();
                    new AlertDialog.Builder(HomeActivity.this)
                            .setMessage("Are you sure you want to exit?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    HomeActivity.this.finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create().show();
                    break;
            }
            return true;
        }
    }


    private void makeHomeDrawer() {
        mHomeDrawer = HomeNavDrawer
                .make(this, this.mOnDrawerClick);
    }

    private void makeSpeedDial() {
        HomeFabSpeedDial.make(this, fabSpeedDial);
    }

    private void addSpeedDialActions() {
        fabSpeedDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fabiPlace:
                        startActivity(new Intent(HomeActivity.this, PlaceNewActivity.class));
                        return false;
                    case R.id.fabiTravel:
                        Intent i = new Intent(HomeActivity.this, Places.class);
                        startActivity(i);
                        return false;
                    case R.id.fabiWander:
                        ToastAdapter.show(getApplicationContext(), "Wander");
                        return false;
                    default:
                        return false;

                }

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        this.makeCustomToolbar();
        // set title.
        getSupportActionBar().setTitle("Happenings");
        //
        this.makeHomeDrawer();
        this.makeSpeedDial();
        this.addSpeedDialActions();


    }

    @Override
    public void onBackPressed() {
        if (mHomeDrawer.isDrawerOpen()) {
            mHomeDrawer.closeDrawer();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            new AlertDialog.Builder(this).setMessage("You searched: " + query)
                    .setPositiveButton("Ok", null)
                    .create().show();
        }
    }


}
