package com.jhmvin.places.feature.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.jhmvin.places.R;
import com.jhmvin.places.TempTravelRunning;

public class NotificationService {

    public final static int ID_FOREGROUND = 1;

    private final Context mServiceContext;
    private final NotificationManagerCompat mNotificationManager;

    public NotificationService(Context context) {
        this.mServiceContext = context;
        mNotificationManager = NotificationManagerCompat.from(context);
    }

    /**
     * Creates a notification builder.
     *
     * @return
     */
    private NotificationCompat.Builder createBuilder() {
        return new NotificationCompat.Builder(mServiceContext);
    }

    /**
     * Returns the notification manager.
     *
     * @return
     */
    public NotificationManagerCompat getNotificationManager() {
        return mNotificationManager;
    }

    public Notification createForegroundNotification() {
        // create an intent when the notification is clicked
        Intent notificationIntent = new Intent(mServiceContext, TempTravelRunning.class);
        // create a pending intent for notif.
        PendingIntent pendingIntent = PendingIntent.getActivity(mServiceContext, 0,
                notificationIntent, 0);
        // create notification
        Notification notification = this.createBuilder().setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Places Tracker")
                .setContentText("Places is tracking your location.")
                .setShowWhen(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();
        // explicit flags add
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR
                | Notification.FLAG_FOREGROUND_SERVICE;

        return notification;
    }
}
