package com.melvinperello.places.ui;

import android.app.SearchManager;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.melvinperello.places.R;

public class SearchToolbarFixer {

    private AppCompatActivity mContext;
    //
    private SearchManager mSearchManager;
    private MenuItem mSearchOptionMenu;
    private SearchView mSearchView;
    private ImageView mSearchCloseButton;
    private Toolbar mCustomSupportToolbar;
    //
    private int mMenuResourceId;
    private Menu mMenu;

    public SearchToolbarFixer(AppCompatActivity mContext) {
        this.mContext = mContext;
    }

    public final static SearchToolbarFixer create(AppCompatActivity context) {
        return new SearchToolbarFixer(context);
    }


    public SearchToolbarFixer addCustomToolbar(@IdRes int resourceId) {
        mCustomSupportToolbar = mContext.findViewById(R.id.toolbar);
        mContext.setSupportActionBar(mCustomSupportToolbar);
        return this;
    }

    public SearchToolbarFixer setMenuResource(@MenuRes int resourceId) {
        this.mMenuResourceId = resourceId;
        return this;
    }

    public SearchToolbarFixer setMenu(Menu menu) {
        this.mMenu = menu;
        return this;
    }


    public SearchToolbarFixer inflate() {
        mContext.getMenuInflater().inflate(this.mMenuResourceId, this.mMenu);
        this.assign();
        return this;
    }

    private SearchToolbarFixer assign() {
        // Associate searchable configuration with the SearchView
        mSearchManager =
                (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        mSearchOptionMenu = this.mMenu.findItem(R.id.action_search);
        mSearchView =
                (SearchView) mSearchOptionMenu.getActionView();
        // full width search view
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setSearchableInfo(
                mSearchManager.getSearchableInfo(mContext.getComponentName()));

        // Get the search close button image view
        mSearchCloseButton = mSearchView.findViewById(R.id.search_close_btn);

        return this;
    }

    public SearchToolbarFixer setCloseButtonAsClear() {
        this.mSearchCloseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get the search text
                EditText mSearchText = mContext.findViewById(R.id.search_src_text);
                //Clear the text from EditText view
                mSearchText.setText("");
                //Clear query
                mSearchView.setQuery("", false);
                //Collapse the action view
                mSearchView.onActionViewCollapsed();
                //Collapse the search widget
                mSearchOptionMenu.collapseActionView();
            }
        });

        return this;
    }

    public SearchToolbarFixer setOnActionExpandListener(MenuItem.OnActionExpandListener listener) {
        // When using the support library, the setOnActionExpandListener() method is
        // static and accepts the MenuItem object as an argument
        mSearchOptionMenu.setOnActionExpandListener(listener);
        return this;
    }


}
