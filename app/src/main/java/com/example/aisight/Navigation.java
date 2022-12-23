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
import java.util.ArrayList;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class Navigation extends AsyncTask <String, Void, String> {

    private ArrayList<String> directions;
    private ArrayList<ArrayList<Double>> stepsCoordinateStack;

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

    public void getDirectionsAndSteps(double dlon, double dlat, double clon, double clat) throws JSONException {
        Client client = ClientBuilder.newClient();
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


    public void distanceBetweenCurrentGPSCoordinateAndLatestDirection(double lon1, double lat1){
        // Calculates the difference between two given coordinates
        double lon2 = stepsCoordinateStack.get(0).get(0);
        double lat2 = stepsCoordinateStack.get(0).get(0);
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        if (dist < 0.1){
            callDirectionAlert();
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
