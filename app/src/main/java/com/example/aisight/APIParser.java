package com.example.aisight;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.json.*;

public class APIParser {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
    // import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
    /* ObjectMapper om = new ObjectMapper();
    Root root = om.readValue(myJsonString, Root.class); */

    JSONObject result;
    JSONArray routes;
    JSONObject underRoutes;
    String a;
    JSONObject summary;
    JSONArray segments;

    static ArrayList<String> instructions = new ArrayList<String>();
    static ArrayList<JSONArray> locations = new ArrayList<JSONArray>();

    public APIParser(String json) throws JSONException {
        result = new JSONObject(json);

        System.out.println(result.toString());

        routes = result.getJSONArray("routes");

        a = routes.get(0).toString();

        underRoutes = new JSONObject(a);

        summary = underRoutes.getJSONObject("summary");

        segments = underRoutes.getJSONArray("segments");

        System.out.println(segments.toString());

        for (int i = 0; i < segments.length(); i++)
        {
            JSONArray steps = segments.getJSONObject(i).getJSONArray("steps");

            for (int j = 0; j < steps.length(); j++) {

                JSONObject maneuver = steps.getJSONObject(j).getJSONObject("maneuver");
                System.out.println(maneuver.toString());

                try {
                    JSONArray location = maneuver.getJSONArray("location");
                    String instruction = steps.getJSONObject(j).getString("instruction");
                    if (instruction != null && location != null) {
                        instructions.add(instruction);
                        locations.add(location);
                    }

                } catch (JSONException exception){

                }

            }

            System.out.println(instructions.toString());
            System.out.println(locations.toString());

        }


    }

    public ArrayList<JSONArray> getLocations() {
        return locations;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

}
