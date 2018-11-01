package com.melvinperello.places.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.melvinperello.places.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class HomeNavDrawer {

    private HomeNavDrawer() {
        // util class
    }

    public final static int ITEM_WANDER = 1;
    public final static int ITEM_TRAVEL = 2;
    public final static int ITEM_PLACE = 3;
    public final static int ITEM_SETTINGS = 4;
    public final static int ITEM_ABOUT = 5;
    public final static int ITEM_EXIT = 6;


    public static Drawer make(AppCompatActivity supportActivity, Drawer.OnDrawerItemClickListener clickListener) {
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem pItemWander = new PrimaryDrawerItem()
                .withIdentifier(ITEM_WANDER)
                .withIcon(FontAwesome.Icon.faw_map)
                .withSelectable(false)
                .withName(R.string.wanders);

        PrimaryDrawerItem pItemTravel = new PrimaryDrawerItem()
                .withIdentifier(ITEM_TRAVEL)
                .withIcon(FontAwesome.Icon.faw_directions)
                .withSelectable(false)
                .withName(R.string.travels);

        PrimaryDrawerItem pItemPlace = new PrimaryDrawerItem()
                .withIdentifier(ITEM_PLACE)
                .withIcon(FontAwesome.Icon.faw_map_marker_alt)
                .withSelectable(false)
                .withName(R.string.places);


        SecondaryDrawerItem sItemSettings = new SecondaryDrawerItem()
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

        //
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(supportActivity)
                .withHeaderBackground(R.drawable.img_default_background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Jhon Melvin Perello").withEmail("jhmvinperello@gmail.com").withIcon(supportActivity.getResources().getDrawable(R.mipmap.ic_launcher))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        Drawer drawer = new DrawerBuilder()
                .withActivity(supportActivity)
                .withAccountHeader(headerResult)
                .withToolbar(((CustomSupportToolbarActivity) supportActivity).getCustomSupportToolbar())
                .addDrawerItems(
                        pItemWander,
                        pItemTravel,
                        pItemPlace,
                        new DividerDrawerItem(),
                        sItemSettings,
                        sItemAbout,
                        sItemExit
                ).withOnDrawerItemClickListener(clickListener)
                .build();
        //
        drawer.deselect();
        return drawer;
    }
}
