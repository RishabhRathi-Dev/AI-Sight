package com.example.aisight;

import android.os.AsyncTask;

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


public class Navigation extends AsyncTask <String, Void, String> {

    private ArrayList<String> directions;
    private ArrayList<ArrayList<Double>> stepsCoordinateStack;

    public static double destinationLat;
    public static double destinationLon;


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

    public void getDirectionsAndSteps(double dlon, double dlat, double clon, double clat) throws JSONException, IOException {

        //Client client = ClientBuilder.newClient();
        JSONObject coordinates = new JSONObject();
        JSONArray topArray = new JSONArray();
        JSONArray current = new JSONArray();
        JSONArray dest = new JSONArray();

        current.put(clon);
        current.put(clat);

        dest.put(dlon);
        dest.put(dlat);

        topArray.put(current);
        topArray.put(dest);

        coordinates.put("coordinates", topArray);
        /*
        Entity<String> payload = Entity.json(coordinates.toString());

        Response response = client.target("https://api.openrouteservice.org/v2/directions/foot-walking/json")
                .request()
                .header("Authorization", "5b3ce3597851110001cf6248157cf6b693c34e94b6420ed637c30b4e")
                .header("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .header("Content-Type", "application/json; charset=utf-8")
                .post(payload);

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));

         */

        // TODO: Make another class to get respose from api


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


    public void distanceBetweenCurrentGPSCoordinateAndLatestDirection(double lon1, double lat1) throws JSONException, IOException {
        // Calculates the difference between two given coordinates
        if (directions == null){
            getDirectionsAndSteps(destinationLon, destinationLat, lon1, lat1);
        }
        else {
            double lon2 = stepsCoordinateStack.get(0).get(0);
            double lat2 = stepsCoordinateStack.get(0).get(0);
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;

            if (dist < 0.1) {
                // ~ 100 m
                callDirectionAlert();
            }
        }
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void callDirectionAlert(){
        // Calls direction update


    }

    public ArrayList<String> getDirections(){
        return directions;
    }
}
