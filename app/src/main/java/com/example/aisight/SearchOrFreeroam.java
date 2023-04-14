package com.example.aisight;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aisight.databinding.ActivitySearchOrFreeroamBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class SearchOrFreeroam extends AppCompatActivity {

    private ActivitySearchOrFreeroamBinding activitySearchOrFreeroamBinding;
    private GetFloatingIconClick mGetServiceClick;
    public static boolean isFloatingIconServiceAlive = false;
    private FragmentManager fm = getSupportFragmentManager();
    CameraFragment fragment;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchOrFreeroamBinding = ActivitySearchOrFreeroamBinding.inflate(getLayoutInflater());
        setContentView(activitySearchOrFreeroamBinding.getRoot());

        // checkPermission();
        mGetServiceClick = new GetFloatingIconClick();


        askDrawOverPermission();

        editText = findViewById(R.id.editText);

        fragment = (CameraFragment) fm.findFragmentById(R.id.camera_container);

        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Speaking alert = new Speaking(SearchOrFreeroam.this, "Hold the button and speak destination");
        // This should fly as this object is being created and not referenced only once it was created so gc should take care of it hopefully preventing memory overflow


        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()); // Sets language according to device language


        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                //getting all the matches
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                //displaying the first match
                if (matches != null)
                    editText.setText(matches.get(0));
                    createFloatingBackButton();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            // Second Search Button
            @Override
            public void onClick(View view) {
                //TODO: Create the new intent here
                createFloatingBackButton();
            }
        });

        findViewById(R.id.button).setOnTouchListener(new View.OnTouchListener() {
            // Mic button
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                        editText.setText("");
                        editText.setHint("Listening...");
                        break;
                }
                return false;
            }
        });
    }

    private class GetFloatingIconClick extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent selfIntent = new Intent(SearchOrFreeroam.this, SearchOrFreeroam.class);
            selfIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(selfIntent);
        }
    }


    private void askDrawOverPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // if OS is pre-marshmallow then create the floating icon, no permission is needed

        } else {
            if (!Settings.canDrawOverlays(this)) {
                // asking for DRAW_OVER permission in settings
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivityForResult(intent, 1122);
            }
        }
    }

    // starting service for creating a floating icon over map
    private void createFloatingBackButton() {
        Intent iconServiceIntent = new Intent(SearchOrFreeroam.this, FloatingOverMapIconService.class);
        //iconServiceIntent.putExtra("RIDE_ID", str_rideId);

        Uri gmmIntentUri = Uri.parse("google.navigation:q="+editText.getText());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);

        iconServiceIntent.setAction("com.example.aisight.action.START");
        startService(iconServiceIntent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1122) {
            // as permissions from Settings don't provide any callbacks, hence checking again for the permission
            // so that we can draw our floating without asking user to click on the previously clicked view
            // again
            if (Settings.canDrawOverlays(this)) {

            } else {
                //permission is not provided by user, do your task
                //GlobalVariables.alert(mContext, "This permission is necessary for this application's functioning");
            }
        } else if (requestCode == 1234) {
            // no result is returned by google map, as google don't provide any apis or documentation
            // for it.
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Need better checkPermission
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)){
                requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 200);
                finish();
            }


        }
    }



}