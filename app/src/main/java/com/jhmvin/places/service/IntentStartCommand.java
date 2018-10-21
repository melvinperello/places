package com.jhmvin.places.service;

import android.content.Intent;

public interface IntentStartCommand {
    void start(Intent intent, int flags, int startId);
}
