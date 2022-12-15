package com.example.aisight;
import android.speech.tts.TextToSpeech;

import java.util.Observable;

abstract class absTextToSpeech extends Observable {
    public static String language = "en"; // Default Language English
    public static TextToSpeech textToSpeech;

    public void setLanguage(String language) {
        synchronized (this) {
            this.language = language;
        }
        setChanged();
        notifyObservers();
    }

    public synchronized String getLanguage() {
        return language;
    }
}
