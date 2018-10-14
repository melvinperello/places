package com.jhmvin.places.service;

import android.content.Intent;

public interface IntentRunnableCommand {
    void run(Intent intent, int flags, int startId);
}
