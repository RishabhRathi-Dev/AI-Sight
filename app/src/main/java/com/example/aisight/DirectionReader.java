package com.example.aisight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class DirectionReader extends AppCompatActivity {

    ArrayList<Double> DestinationCoords;
    ArrayList<String> directions = new ArrayList<String>();
    private LocationService mLocationService;
    private Intent mServiceIntent;
    Navigation n = new Navigation();
    LinearLayout dContainer;

    private Long wait = Long.MAX_VALUE;

    public void fillDirections(){

        if (n.isDirectionAvailable()){
            directions = n.getDirections();
            for (String dir : directions) {
                // Creates directions
                if (dContainer.getChildCount() < directions.size()) {
                    TextView stepName = new TextView(this);
                    stepName.setText(dir);
                    stepName.setTextSize(20);
                    stepName.setPadding(5, 5, 5, 5);
                    stepName.setGravity(Gravity.CENTER);
                    stepName.setTypeface(null, Typeface.BOLD);
                    dContainer.addView(stepName);
                }
            }
            wait = Long.MIN_VALUE;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction_reader);
        dContainer = (LinearLayout) findViewById(R.id.DirectionContainer);

        String Destination = getIntent().getStringExtra("Destination");
        AsyncTask<String, Void, String> sCoords = n.execute(Destination);
        try {
            String result = sCoords.get();
            if (result != null) {
                DestinationCoords = n.convertedToLatLong(result);
                startServiceFunc();
                CountDownTimer t = new CountDownTimer(wait , 3000) {
                    public void onTick(long millisUntilFinished) {
                        fillDirections();
                    }

                    public void onFinish() {
                        Log.d("test","Timer last tick");
                        start();
                    }
                }.start();
                //System.out.println(mLocationService.getLatest());
            }
            else {
                Speaking wrongInput = new Speaking(DirectionReader.this, "Please Try Again");
                Intent back = new Intent(DirectionReader.this, SearchOrFreeroam.class);
                startActivity(back);
                stopServiceFunc();
                finish();
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

}

    private void startServiceFunc(){
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (!isMyServiceRunning(mLocationService.getClass(), this)) {
            startService(mServiceIntent);
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Service Already Running", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopServiceFunc(){
        mLocationService = new LocationService();
        mServiceIntent = new Intent(this, mLocationService.getClass());
        if (isMyServiceRunning(mLocationService.getClass(), this)) {
            stopService(mServiceIntent);
            Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show();
            //saveLocation(); // explore it by your self
        } else {
            Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, DirectionReader directionReader) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}