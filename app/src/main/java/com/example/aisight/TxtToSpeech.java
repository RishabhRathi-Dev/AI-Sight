package com.example.aisight;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class TxtToSpeech extends absTextToSpeech implements Observer {

    public TxtToSpeech() {
        textToSpeech.setLanguage(Locale.forLanguageTag(language));
    };

    public void speak(String text){
        textToSpeech.speak(text,textToSpeech.QUEUE_FLUSH, null, null);
    }

    public void observe(Observable o) {
        o.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {
        // Updates Language for text to speech
        textToSpeech.setLanguage(Locale.forLanguageTag(language));
    }
}
