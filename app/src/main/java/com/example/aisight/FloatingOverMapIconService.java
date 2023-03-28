package com.example.aisight;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatingOverMapIconService extends Service {
    public FloatingOverMapIconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}