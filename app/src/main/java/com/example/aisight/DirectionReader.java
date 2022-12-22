package com.example.aisight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DirectionReader extends AppCompatActivity {

    EditText Text;
    Button btnText;
    TextToSpeech textToSpeech;
    Double[] DestinationCoords;

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
            DestinationCoords = n.convertedToLatLong(result);
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
    }
}