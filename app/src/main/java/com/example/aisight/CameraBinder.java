package com.example.aisight;

import android.content.Context;
import android.view.OrientationEventListener;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;

public class CameraBinder implements ImageAnalysis.Analyzer {
    private final ProcessCameraProvider cameraProvider;
    private final Context context;
    private final ImageAnalyzer imageAnalyzer;

    public CameraBinder(ProcessCameraProvider cameraProvider,
                        Context context, ImageAnalyzer imageAnalyzer) {
        this.cameraProvider = cameraProvider;
        this.context = context;
        this.imageAnalyzer = imageAnalyzer;
    }

    public void setUp(final LifecycleOwner lifecycleOwner) {
        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();
        // Choose the camera by requiring a lens facing
        final int lens = CameraSelector.LENS_FACING_FRONT;
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lens)
                .build();
        // Image Analysis use case
        // Use cases
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build();
        // Get orientation
        // Source: https://stackoverflow.com/a/59894580/4544940
        OrientationEventListener orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int orientation) {
                int rotation;

                // Monitors orientation values to determine the target rotation value
                if (orientation >= 45 && orientation < 135) {
                    rotation = Surface.ROTATION_270;
                } else if (orientation >= 135 && orientation < 225) {
                    rotation = Surface.ROTATION_180;
                } else if (orientation >= 225 && orientation < 315) {
                    rotation = Surface.ROTATION_90;
                } else {
                    rotation = Surface.ROTATION_0;
                }

                // Updates target rotation value to {@link ImageAnalysis}
                imageAnalysis.setTargetRotation(rotation);
            }
        };
        orientationEventListener.enable();
        imageAnalysis.setAnalyzer(Runnable::run, this);
        // Attach use cases to the camera with the same lifecycle owner
        Camera camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getImageInfo().getTimestamp() % 10 == 0) {
            this.imageAnalyzer.run(image);
        }
        image.close();
    }
}
