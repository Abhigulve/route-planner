package com.tomtom.demo.core;

import com.google.gson.JsonElement;
import com.tomtom.demo.APIConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RouteUtils {

    public static List<String> splitAndGenarateUrl(String pois) {

        String[] poi = pois.split(",");
        List<String> urls = new ArrayList<>();
        for (String s : poi) {
            urls.add(s);
        }
        return urls;
    }

    public static String counstructReviewUlr(JsonElement poi, JsonElement position) {
        String poiName = poi.getAsJsonObject().get("name").toString().replace("\"", "");

        String param1After = null;
        try {
            param1After = URLEncoder.encode(poiName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String lat = position.getAsJsonObject().get("lat").toString().replace("\"", "");
        String lon = position.getAsJsonObject().get("lon").toString().replace("\"", "");
        String url = APIConstant.REWIEW + param1After + "&locationbias=circle:2000@" + lat + "," + lon;
        return url;
    }
}
