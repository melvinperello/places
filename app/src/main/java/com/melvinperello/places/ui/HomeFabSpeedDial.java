package com.melvinperello.places.ui;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.melvinperello.places.R;

public class HomeFabSpeedDial {

    public final static void make(Context context, com.leinardi.android.speeddial.SpeedDialView fabSpeedDial) {
        fabSpeedDial.setMainFabClosedBackgroundColor(context.getResources().getColor(R.color.primary));
        fabSpeedDial.setMainFabOpenedBackgroundColor(context.getResources().getColor(R.color.primary_dark));
        SpeedDialActionItem fabiWander = new SpeedDialActionItem.Builder(R.id.fabiWander, R.drawable.ic_map_white_24dp)
                .setLabel(R.string.wander)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.fabiColorBlue, context.getTheme()))
                .setLabelColor(context.getResources().getColor(R.color.textBlack))
                .setLabelClickable(true)
                .create();

        SpeedDialActionItem fabiTravel = new SpeedDialActionItem.Builder(R.id.fabiTravel, R.drawable.ic_directions_white_24dp)
                .setLabel(R.string.travel)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.fabiColorGreen, context.getTheme()))
                .setLabelColor(context.getResources().getColor(R.color.textBlack))
                .setLabelClickable(true)
                .create();

        SpeedDialActionItem fabiPlace = new SpeedDialActionItem.Builder(R.id.fabiPlace, R.drawable.ic_place_white_24dp)
                .setLabel(R.string.place)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.fabiColorRed, context.getTheme()))
                .setLabelColor(context.getResources().getColor(R.color.textBlack))
                .setLabelClickable(true)
                .create();

        fabSpeedDial.addActionItem(fabiPlace);
        fabSpeedDial.addActionItem(fabiTravel);
        fabSpeedDial.addActionItem(fabiWander);
    }
}
