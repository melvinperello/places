package com.melvinperello.places.ui;

import android.support.v7.app.AppCompatActivity;

import com.melvinperello.places.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

public class HomeNavDrawer {

    private HomeNavDrawer() {
        // util class
    }

    public final static int ITEM_SETTINGS = 1;
    public final static int ITEM_ABOUT = 2;
    public final static int ITEM_EXIT = 3;


    public static Drawer make(AppCompatActivity supportActivity, Drawer.OnDrawerItemClickListener clickListener) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem pItemSettings = new PrimaryDrawerItem()
                .withIdentifier(ITEM_SETTINGS)
                .withIcon(FontAwesome.Icon.faw_cog)
                .withSelectable(false)
                .withName(R.string.settings);

        SecondaryDrawerItem sItemAbout = new SecondaryDrawerItem()
                .withIdentifier(ITEM_ABOUT)
                .withIcon(FontAwesome.Icon.faw_info_circle)
                .withSelectable(false)
                .withName(R.string.about);
        SecondaryDrawerItem sItemExit = new SecondaryDrawerItem()
                .withIdentifier(ITEM_EXIT)
                .withSelectable(false)
                .withIcon(FontAwesome.Icon.faw_external_link_alt)
                .withName(R.string.exit);


        return new DrawerBuilder()
                .withActivity(supportActivity)
                .withToolbar(((CustomSupportToolbarActivity) supportActivity).getCustomSupportToolbar())
                .addDrawerItems(
                        pItemSettings,
                        new DividerDrawerItem(),
                        sItemAbout,
                        sItemExit
                ).withOnDrawerItemClickListener(clickListener)
                .build();
    }
}
