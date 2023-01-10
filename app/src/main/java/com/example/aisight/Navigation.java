package com.example.aisight;

import android.app.Activity;
import android.app.Service;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Navigation extends AsyncTask <String, Void, String> {

    private static ArrayList<String> directions = new ArrayList<>();
    private static ArrayList<JSONArray> stepsCoordinateStack = new ArrayList<JSONArray>();

    public static double destinationLat;
    public static double destinationLon;

    private static boolean calledAPI = false;

    APITalker apiTalker = new APITalker();

    private static int step = 0;


    public Navigation() {

    }

    @Override
    protected String doInBackground(String... strings) {
        String coordi = null;
        String url = "https://geocode.xyz/"+strings[0];
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            // Get material in div class = Z0LcW t2b5Cf as it contains coordinates
            Elements coordinates = doc.select("a");
            int count = 0;
            for (Element coords : coordinates) {
                if (count == 1) {
                    coordi = coords.text();
                    break;
                }
                count++;

            }
            System.out.println(coordi);
            return coordi;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public void getDirectionsAndSteps(double dlon, double dlat, double clon, double clat, Service asker) throws JSONException, IOException, InterruptedException {

        apiTalker.talk(dlon, dlat, clon, clat, asker);
        TimeUnit.SECONDS.sleep(4); // To give enough time to get response and arrays needed
        directions = apiTalker.getDirections();
        stepsCoordinateStack = apiTalker.getStepsCoordinateStack();

        //System.out.println("NAVIGATION");
        //System.out.println(directions.toString());

    }

    public ArrayList<Double> convertedToLatLong(String coordinates){
        // This function converts extracted coordinates into usable format
        // Lat, lon

        ArrayList<Double> coords = new ArrayList<Double>();
        String lat = coordinates.substring(0, coordinates.indexOf(','));
        String lon = coordinates.substring(coordinates.indexOf(',')+1);
        destinationLat = Double.parseDouble(lat);
        destinationLon = Double.parseDouble(lon);
        coords.add(destinationLat);
        coords.add(destinationLon);
        return coords;
    }


    public void distanceBetweenCurrentGPSCoordinateAndLatestDirection(double lon1, double lat1, LocationService asker) throws JSONException, IOException, InterruptedException {
        // Calculates the difference between two given coordinates
        if (!calledAPI){
            getDirectionsAndSteps(destinationLon, destinationLat, lon1, lat1, asker);
            calledAPI = true;
        }
        else {

            //System.out.println("ELSE CALLED");

            if (!stepsCoordinateStack.isEmpty() || !directions.isEmpty()) {
                double lon2 = stepsCoordinateStack.get(step).getDouble(0);
                double lat2 = stepsCoordinateStack.get(step).getDouble(1);
                double theta = lon1 - lon2;
                double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
                dist = Math.acos(dist);
                dist = rad2deg(dist);
                dist = dist * 60 * 1.1515;

                dist = dist * 1.609344;

                System.out.println(dist);

                if (dist < 0.01) {
                    // ~ 10 m
                    callDirectionAlert(asker);
                }
            }
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void callDirectionAlert(LocationService parent){
        // Calls direction update
        Log.d("DIRECTION", "Alert called");
        Speaking say = new Speaking(parent, directions.get(step));
        step++;
    }

    public boolean isDirectionAvailable() {return !directions.isEmpty();}

    public ArrayList<String> getDirections(){
        return directions;
    }
}
