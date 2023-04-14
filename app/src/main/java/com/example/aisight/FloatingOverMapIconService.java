package com.example.aisight;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.contentcapture.ContentCaptureContext;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FloatingOverMapIconService extends LifecycleService implements ObjectDetectorHelper.DetectorListener {
    private WindowManager windowManager;
    private FrameLayout frameLayout;
    private SurfaceView surfaceView;
    private String str_ride_id;
    public static final String BROADCAST_ACTION = "com.example.SearchOrFreeroam";
    Long then = 0l;

    private String TAG = "ObjectDetectionService";
    private ObjectDetectorHelper objectDetectorHelper;
    private Bitmap bitmapBuffer;
    private Preview preview;
    private ImageAnalysis imageAnalyzer;
    private Camera camera;
    private ProcessCameraProvider cameraProvider;
    private PreviewView pp;

    private ExecutorService cameraExecutor;


    @SuppressLint("MissingSuperCall")
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        cameraExecutor = Executors.newSingleThreadExecutor();
        objectDetectorHelper = new ObjectDetectorHelper(0.6f, 2, 3, 0, 0, getApplicationContext(), this);

        createNotificationChannel();

        Intent notificationIntent = new Intent(this, SearchOrFreeroam.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(
                this,
                "AISIGHT"
        )
                .setContentTitle(getString(R.string.bg_service_title))
                .setContentText(getString(R.string.bg_service_text))
                .setSmallIcon(R.drawable.bg_service_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        createFloatingBackButton();
    }

    private void setUpCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // CameraProvider
                    cameraProvider = cameraProviderFuture.get();

                    // Build and bind the camera use cases
                    bindCameraUseCases();
                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions
                }
            }
        }, ContextCompat.getMainExecutor(this));



    }

    private void bindCameraUseCases() {
        // CameraProvider
        ProcessCameraProvider cameraProvider = Objects.requireNonNull(this.cameraProvider, "Camera initialization failed.");

        // CameraSelector - makes assumption that we're only using the back camera
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview = new Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3).build();

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer = new ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build();

        // The analyzer can then be assigned to the instance
        imageAnalyzer.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                // The image rotation and RGB image buffer are initialized only once
                // the analyzer has started running
                bitmapBuffer = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
                detectObjects(image);
                image.close();
            }
        });

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer);

            // Attach the viewfinder's surface provider to preview use case
            Preview.SurfaceProvider surfaceProvider = pp.getSurfaceProvider();
            preview.setSurfaceProvider(cameraExecutor, surfaceProvider);
        } catch (Exception exc) {
            Log.e(TAG, "Use case binding failed", exc);
        }

    }

    private void detectObjects(ImageProxy image) {
        Image.Plane[] planes = (Image.Plane[]) image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        int imageRotation = image.getImageInfo().getRotationDegrees();
// Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmap, imageRotation);


    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel("AISIGHT", "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // to receive any data from activity
        str_ride_id = intent.getStringExtra("RIDE_ID");
        setUpCamera();
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

        pp = frameLayout.findViewById(R.id.viewfinderinservice);

        ImageView backOnMap = frameLayout.findViewById(R.id.custom_drawover_back_button);
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

        backOnMap.setOnTouchListener((v, event) -> {

            if(event.getAction() == MotionEvent.ACTION_DOWN){
                then = (Long) System.currentTimeMillis();
            }
            else if(event.getAction() == MotionEvent.ACTION_UP){
                if(((Long) System.currentTimeMillis() - then) > 3000){
                    Speaking say = new Speaking(FloatingOverMapIconService.this, "Calling Emergency");
                    //TODO :: Create the implementation of a emergency message intent and some form of animation/vibration to show whether ready to send emergency
                    return true;
                }
            }
            return false;
        });

        windowManager.addView(frameLayout, params);
    }

    @Override
    public void onError(@NonNull String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResults(@Nullable List<Detection> results, long inferenceTime, int imageHeight, int imageWidth) {
        Speaking say = new Speaking(this, "Result");


    }
}