package com.example.aisight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DirectionReader extends AppCompatActivity {

    EditText Text;
    Button btnText;
    TextToSpeech textToSpeech;
    ArrayList<Double> DestinationCoords;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double wayLatitude = 0.0, wayLongitude = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_reader);

        Text = findViewById(R.id.Text);
        btnText = findViewById(R.id.btnText);
        String Destination = getIntent().getStringExtra("Destination");

        Navigation n = new Navigation();
        AsyncTask<String, Void, String> sCoords = n.execute(Destination);
        try {
            String result = sCoords.get();
            if (result != null) {
                DestinationCoords = n.convertedToLatLong(result);
            }
            else {
                Speaking wrongInput = new Speaking(DirectionReader.this, "Please Try Again");
                Intent back = new Intent(DirectionReader.this, SearchOrFreeroam.class);
                startActivity(back);
                finish();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Adding OnClickListener
        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Speaking read = new Speaking(DirectionReader.this, Text.getText().toString());
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        n.distanceBetweenCurrentGPSCoordinateAndLatestDirection(wayLongitude, wayLatitude);
                    }
                }
            }
        };
    }
}