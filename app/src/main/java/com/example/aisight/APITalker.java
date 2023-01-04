package com.example.aisight;

import android.app.Service;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class APITalker {

    JSONObject coordinates = new JSONObject();
    JSONArray topArray = new JSONArray();
    JSONArray current = new JSONArray();
    JSONArray dest = new JSONArray();
    private final OkHttpClient client = new OkHttpClient();

    private ArrayList<String> directions;
    private ArrayList<ArrayList<Double>> stepsCoordinateStack;


    public APITalker() {}

    public void resposeParser(String response) throws ParseException, JSONException {
        Gson gson = new Gson();
        APIParser apiParser = gson.fromJson(response, APIParser.class);

    }

    public ArrayList<String> talk(double dlon, double dlat, double clon, double clat, Service asker) throws JSONException, IOException {
        // TODO: parse response and return direction and also limit calling

        current.put(clon);
        current.put(clat);

        dest.put(dlon);
        dest.put(dlat);

        topArray.put(current);
        topArray.put(dest);

        coordinates.put("coordinates", topArray);
        coordinates.put("maneuvers", "true");
        coordinates.put("geometry", "false");

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(coordinates.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openrouteservice.org/v2/directions/foot-walking/json")
                .header("Authorization", "5b3ce3597851110001cf6248157cf6b693c34e94b6420ed637c30b4e")
                .addHeader("Accept", "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    resposeParser(response.body().string());
                } catch (ParseException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("response", call.request().body().toString());
            }
        });


        return directions;
    }


}
