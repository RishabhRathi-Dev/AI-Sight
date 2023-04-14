package com.example.aisight;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class Speaking {
    TextToSpeech textToSpeech;

    public Speaking(Activity caller, String text) {
        textToSpeech = new TextToSpeech(caller.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.getDefault()); // sets language of device
                    say(text);
                }
            }
        });
    }

    public Speaking(LocationService parent, String text) {
        textToSpeech = new TextToSpeech(parent.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.getDefault()); // sets language of device
                    say(text);
                }
            }
        });
    }

    public Speaking(FloatingOverMapIconService parent, String text) {
        textToSpeech = new TextToSpeech(parent.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.getDefault()); // sets language of device
                    say(text);
                }
            }
        });
    }

    private void say(String text) {
        textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

}
