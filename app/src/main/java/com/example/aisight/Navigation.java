package com.example.aisight;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;

public class Navigation extends AsyncTask <String, Void, String> {

    private String[] directions;

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

    public Double[] convertedToLatLong(String coordinates){
        // This function converts extracted coordinates into usable format

        Double[] coords = new Double[2];



        return coords;
    }

    public void getCoordinateOfDestination (String Destination) throws IOException {
        // Webscrapes the coordinate of Destination
        String coordi;
        String url = "https://www.google.com/search?q="+Destination;
        Document doc = Jsoup.connect(url).get();
        // Get material in div class = Z0LcW t2b5Cf
        Elements coordinates = doc.getElementsByClass("Z0LcW t2b5Cf");
        for (Element coords : coordinates) {
            coordi = coords.text();
        }
    }

    public void distanceBetweenCurrentGPSCoordinateAndLatestDirection(){
        // Calculates the difference between two given coordinates
    }

    public void callDirectionAlert(){
        // Determines weather to call for update sound or not
    }

    public String[] getDirections(){
        return directions;
    }
}
