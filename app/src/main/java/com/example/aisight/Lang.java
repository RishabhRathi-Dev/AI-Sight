package com.example.aisight;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lang extends absTextToSpeech {

    private static Locale[] locales = Locale.getAvailableLocales();
    private List<Locale> localeList = new ArrayList<Locale>();

    public Lang(){
        for (Locale locale : locales) {
            int res = textToSpeech.isLanguageAvailable(locale);
            if (res == textToSpeech.LANG_COUNTRY_AVAILABLE) {
                localeList.add(locale);
            }
        }

    }

    public void setNewLanguage(String newLang){
        setLanguage(newLang);
    }

    public List<Locale> getLocaleList(){
        return localeList;
    }
}
