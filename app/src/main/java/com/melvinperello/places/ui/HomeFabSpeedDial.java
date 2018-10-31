package com.melvinperello.places.ui;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.melvinperello.places.R;

public class HomeFabSpeedDial {

    public final static void make(Context context, com.leinardi.android.speeddial.SpeedDialView fabSpeedDial) {
        fabSpeedDial.setMainFabClosedBackgroundColor(context.getResources().getColor(R.color.primary));
        fabSpeedDial.setMainFabOpenedBackgroundColor(context.getResources().getColor(R.color.primary_dark));
        SpeedDialActionItem discovery = new SpeedDialActionItem.Builder(R.id.fabSpeedDialDiscovery, R.drawable.ic_map_white_24dp)
                .setLabel("Discovery")
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.fabSpeedDialBlue, context.getTheme()))
                .setLabelColor(context.getResources().getColor(R.color.textBlack))
                .setLabelClickable(true)
                .create();

        SpeedDialActionItem journey = new SpeedDialActionItem.Builder(R.id.fabSpeedDialJourney, R.drawable.ic_directions_white_24dp)
                .setLabel("Journey")
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.fabSpeedDialGreen, context.getTheme()))
                .setLabelColor(context.getResources().getColor(R.color.textBlack))
                .setLabelClickable(true)
                .create();
        fabSpeedDial.addActionItem(journey);
        fabSpeedDial.addActionItem(discovery);
    }
}
