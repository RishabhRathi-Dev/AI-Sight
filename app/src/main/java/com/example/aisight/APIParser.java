package com.example.aisight;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class APIParser {
    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
    // import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
    /* ObjectMapper om = new ObjectMapper();
    Root root = om.readValue(myJsonString, Root.class); */

    public class Engine{
        public String version;
        public Date build_date;
        public Date graph_date;
    }

    public class Maneuver{
        public ArrayList<Double> location;
        public int bearing_before;
        public int bearing_after;
    }

    public class Metadata{
        public String attribution;
        public String service;
        public long timestamp;
        public Query query;
        public Engine engine;
    }

    public class Query{
        public ArrayList<ArrayList<Double>> coordinates;
        public String profile;
        public String format;
        public boolean maneuvers;
    }

    public class Root{
        public ArrayList<Route> routes;
        public ArrayList<Double> bbox;
        public Metadata metadata;
    }

    public class Route{
        public Summary summary;
        public ArrayList<Segment> segments;
        public ArrayList<Double> bbox;
    }

    public class Segment{
        public double distance;
        public double duration;
        public ArrayList<Step> steps;
    }

    public class Step{
        public double distance;
        public double duration;
        public int type;
        public String instruction;
        public String name;
        public ArrayList<Integer> way_points;
        public Maneuver maneuver;
    }

    public class Summary{
        public double distance;
        public double duration;
    }

}
