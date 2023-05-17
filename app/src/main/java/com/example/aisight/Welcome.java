package com.example.aisight;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    private static int SPLASH_TIMED_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(Welcome.this, LogIn.class);
                startActivity(loginIntent);
                finish();
            }
        },SPLASH_TIMED_OUT);
    }
}


