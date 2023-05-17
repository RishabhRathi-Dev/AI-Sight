package com.example.aisight;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.concurrent.Executor;

public class LogIn extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView authStatusTv;
    private Button authBtn;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private TextToSpeech textToSpeech;

    private static final int TTS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        authStatusTv = findViewById(R.id.authStatusTv);
        authBtn = findViewById(R.id.authBtn);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LogIn.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Error authenticating, stop tasks that require authentication
                authStatusTv.setText("Authentication error: " + errString);
                Toast.makeText(LogIn.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Authentication succeeded
                authStatusTv.setText("Authentication succeeded...!");
                Toast.makeText(LogIn.this, "Authentication succeeded...!", Toast.LENGTH_SHORT).show();

                // Start new activity
                Intent intent = new Intent(LogIn.this, SearchOrFreeroam.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Failed authenticating
                authStatusTv.setText("Authentication failed...!");
                Toast.makeText(LogIn.this, "Authentication failed...!", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Login using fingerprint authentication")
                .setNegativeButtonText("User App Password")
                .build();

        textToSpeech = new TextToSpeech(this, this);

        authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String loginMessage = "Please authenticate using fingerprint";
                textToSpeech.speak(loginMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Text-to-speech language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Text-to-speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
