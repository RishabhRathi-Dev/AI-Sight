package com.example.aisight;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

public class Navigation extends AsyncTask <String, Void, String> {

    private ArrayList<String> directions;

    public Navigation() {

    }

    @Override
    protected String doInBackground(String... strings) {
        String coordi = null;
        String url = "https://www.google.com/search?q="+strings[0]+" coordinates";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            // Get material in div class = Z0LcW t2b5Cf as it contains coordinates
            Elements coordinates = doc.getElementsByClass("Z0LcW t2b5Cf");
            for (Element coords : coordinates) {
                coordi = coords.text();
            }
            return coordi;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public ArrayList<Double> convertedToLatLong(String coordinates){
        // This function converts extracted coordinates into usable format

        ArrayList<Double> coords = new ArrayList<Double>();
        String North = coordinates.substring(0, coordinates.indexOf('°'));
        String inter = coordinates.substring(coordinates.indexOf(','));
        String East = inter.substring(inter.indexOf(' ')+1, inter.indexOf('°'));
        coords.add(Double.parseDouble(North));
        coords.add(Double.parseDouble(East));
        return coords;
    }


    public void distanceBetweenCurrentGPSCoordinateAndLatestDirection(){
        // Calculates the difference between two given coordinates
    }

    public void callDirectionAlert(){
        // Determines weather to call for update sound or not
    }

    public ArrayList<String> getDirections(){
        return directions;
    }
}
