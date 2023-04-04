package com.example.aisight;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class BgObjectDetectionService extends LifecycleService {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    //private FrameAnalyzer frameAnalyzer;

    public BgObjectDetectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Intent notificationIntent = new Intent(this, SearchOrFreeroam.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle(getString(R.string.bg_service_title))
                .setContentText(getString(R.string.bg_service_text))
                .setSmallIcon(R.drawable.bg_service_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        // Add listener for Camera Provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                CameraBinder binder = new CameraBinder(cameraProvider, getApplicationContext(), frameAnalyzer);
                binder.setUp(this);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}