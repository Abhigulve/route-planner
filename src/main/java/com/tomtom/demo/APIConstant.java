package com.tomtom.demo;

import java.util.HashMap;
import java.util.Map;

public final class APIConstant {

    public static String BasePoiUrl = "TOM TOM API for getting POI";
    public static String REWIEW = "Google API for getting Review ";
    public static Map<String, String> apiMap = new HashMap<>();

    static {
        apiMap.put("hotel", "hotel.json");
        apiMap.put("casino", "casino.json");
    }

    public static String getApi(String key) {
        return apiMap.get(key);
    }

}
//input=mongolian%20grill
//&locationbias=circle:2000@47.6918452,-122.2226413