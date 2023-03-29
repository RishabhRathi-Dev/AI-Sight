package com.example.aisight;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class FloatingOverMapIconService extends Service {
    private WindowManager windowManager;
    private FrameLayout frameLayout;
    private SurfaceView surfaceView;
    private String str_ride_id;
    public static final String BROADCAST_ACTION = "com.example.SearchOrFreeroam";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatingBackButton();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // to receive any data from activity
        str_ride_id = intent.getStringExtra("RIDE_ID");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(frameLayout);
    }

    private void createFloatingBackButton() {

        //CurrentJobDetail.isFloatingIconServiceAlive = true;

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }


        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);

        frameLayout = new FrameLayout(this);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Here is the place where you can inject whatever layout you want in the frame layout
        layoutInflater.inflate(R.layout.custom_start_ride_back_button_over_map, frameLayout);

        ImageView backOnMap = (ImageView) frameLayout.findViewById(R.id.custom_drawover_back_button);
        backOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatingOverMapIconService.this, SearchOrFreeroam.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //stopping the service
                FloatingOverMapIconService.this.stopSelf();
                //CurrentJobDetail.isFloatingIconServiceAlive = false;
            }
        });

        windowManager.addView(frameLayout, params);
    }
}