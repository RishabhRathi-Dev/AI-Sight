package com.example.aisight

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.aisight.databinding.FragmentCameraBinding
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BackgroundObjectDetectionService : Service(), ObjectDetectorHelper.DetectorListener {

    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    override fun onCreate() {
        super.onCreate()

        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            objectDetectorListener = this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        // Run the object detection on the bitmap buffer in the background
        val bitmapCopy = bitmapBuffer.copy(Bitmap.Config.ARGB_8888, false)
        AsyncTask.execute {
            objectDetectorHelper.detectObjects(bitmapCopy)
        }
    }

    override fun onObjectsDetected(detections: List<Detection>) {
        // Do something with the detected objects
    }
}

}