package com.jhmvin.places.util;

import android.content.Context;

import com.sdsmdg.tastytoast.TastyToast;

/**
 * Toast Interface.
 */
public class ToastInterface {
    public final static int DEFAULT = TastyToast.DEFAULT;
    public final static int SUCCESS = TastyToast.SUCCESS;
    public final static int WARNING = TastyToast.WARNING;
    public final static int ERROR = TastyToast.ERROR;
    public final static int INFO = TastyToast.INFO;
    public final static int UNKNOWN = TastyToast.CONFUSING;

    public final static int LENGTH_SHORT = TastyToast.LENGTH_SHORT;
    public final static int LENGTH_LONG = TastyToast.LENGTH_LONG;


    public static void show(Context context, String text, int type, int length) {
        TastyToast.makeText(context, text, length, type);
    }

    public static void show(Context context, String text, int type) {
        TastyToast.makeText(context, text, LENGTH_SHORT, type);
    }

    public static void show(Context context, String text) {
        TastyToast.makeText(context, text, LENGTH_SHORT, DEFAULT);
    }
}
