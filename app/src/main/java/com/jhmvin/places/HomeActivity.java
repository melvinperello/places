package com.jhmvin.places;

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
import android.view.inputmethod.InputMethodManager;

import com.jhmvin.places.ui.CustomSupportToolbarActivity;
import com.jhmvin.places.ui.HomeNavDrawer;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

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

    /**
     * Loads the custom toolbar.
     */
    private void loadCustomToolbar() {
        // create custom toolbar
        this.mCustomSupportToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.mCustomSupportToolbar);
        getSupportActionBar().setTitle(R.string.places);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        this.loadCustomToolbar();

        mHomeDrawer = HomeNavDrawer
                .build(this, new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case HomeNavDrawer.ITEM_SETTINGS:
                                mHomeDrawer.closeDrawer();
                                new AlertDialog.Builder(HomeActivity.this)
                                        .setMessage("Settings")
                                        .setPositiveButton("OK", null)
                                        .create().show();
                                mHomeDrawer.closeDrawer();
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
                });


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


//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    showInputMethod(view.findFocus());
//                }
//            }
//        });

        return true;
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
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
