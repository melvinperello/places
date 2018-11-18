package com.melvinperello.places.ui;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.melvinperello.places.R;

/**
 * Search Capable Activity.
 */
public class AppCompatSearchActivity extends AppCompatActivity implements
        CustomSupportToolbarActivity, MenuItem.OnActionExpandListener,
        View.OnClickListener {
    /*
    Manifest Activity Template
        <activity
            android:name=".PlaceViewActivity"
            android:launchMode="singleTop"
            android:theme="@style/AppTheme.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.SEARCH" />
            </intent-filter>
            <meta-data
                android:name="android.app.searchable"
                android:resource="@xml/searchable" />
        </activity>
     */

    //----------------------------------------------------------------------------------------------
    // Members
    //----------------------------------------------------------------------------------------------
    private SearchManager mSearchManager;
    private MenuItem mSearchOptionMenu;
    private SearchView mSearchView;
    private ImageView mSearchCloseButton;
    private Toolbar mCustomSupportToolbar;

    public SearchManager getSearchManager() {
        return mSearchManager;
    }

    public MenuItem getSearchOptionMenu() {
        return mSearchOptionMenu;
    }

    public SearchView getSearchView() {
        return mSearchView;
    }

    public ImageView getSearchCloseButton() {
        return mSearchCloseButton;
    }


    @Override
    public Toolbar getCustomSupportToolbar() {
        return mCustomSupportToolbar;
    }

    public void delayedAffixSearchToolbar() {
        this.mCustomSupportToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.mCustomSupportToolbar);
    }

    /**
     * Override this for different menu resource.
     *
     * @param menu
     */
    public void inflateSearchActionMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_search, menu);
    }

    public void createSearchComponents(Menu menu) {
        // Associate searchable configuration with the SearchView
        mSearchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchOptionMenu = menu.findItem(R.id.action_search);
        mSearchView =
                (SearchView) mSearchOptionMenu.getActionView();
        // full width search view
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setSearchableInfo(
                mSearchManager.getSearchableInfo(getComponentName()));

        // Get the search close button image view
        mSearchCloseButton = mSearchView.findViewById(R.id.search_close_btn);

    }



    private void setCloseButtonAsClear() {
        // Set on click listener
        mSearchCloseButton.setOnClickListener(this);
    }

    private void setActionExpandCallback() {
        // When using the support library, the setOnActionExpandListener() method is
        // static and accepts the MenuItem object as an argument
        mSearchOptionMenu.setOnActionExpandListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate layout
        this.inflateSearchActionMenu(menu);
        // find components
        this.createSearchComponents(menu);
        // close as clear
        this.setCloseButtonAsClear();
        // collapse listener
        this.setActionExpandCallback();

        return true; // display options menu
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true; // Return true to expand action view
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true; // Return true to collapse action view
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mSearchCloseButton)) {
            // get the search text
            EditText  mSearchText = findViewById(R.id.search_src_text);
            //Clear the text from EditText view
            mSearchText.setText("");
            //Clear query
            mSearchView.setQuery("", false);
            //Collapse the action view
            mSearchView.onActionViewCollapsed();
            //Collapse the search widget
            mSearchOptionMenu.collapseActionView();
        }
    }
}
