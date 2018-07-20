package com.tomtom.demo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tomtom.demo.core.RouteUtils;
import org.springframework.web.bind.annotation.*;
import texttospeech.SpeechUtils;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GetPoi {

    private JsonArray result;

    @Produces("application/json")
    @CrossOrigin(value = "true")
    @RequestMapping("/getpoi/{type}")
    public String getPoi(@PathVariable("type") String type, @QueryParam("lat") String lat, @QueryParam("lon") String lon, @QueryParam("radius") String radius, @QueryParam("limit") String limit) throws UnsupportedEncodingException {
        RouteUtils.splitAndGenarateUrl(type);
        List<String> strings = RouteUtils.splitAndGenarateUrl(type);
        JsonObject result = new JsonObject();
        JsonArray resultArray = new JsonArray();
        JsonObject jsonObject = null;
        for (String s : strings) {
            String encode = URLEncoder.encode(s, "UTF-8");
            String finalURL = APIConstant.BasePoiUrl + encode + ".json" + "?key=aIbT4MtEJDnC5GisO79KzEOjSuPB6pXI&language=en-GB&radius=" + radius + "&limit=" + 6 + "&lat=" +
                    lat + "&lon=" + lon;
            jsonObject = new Gson().fromJson(HttpConnection.hitHTTPConnection(finalURL), JsonObject.class);
            reviewAndImage(jsonObject, resultArray);
            result.add(s.replace(".json", ""), jsonObject);
        }
        JsonObject result1 = new JsonObject();
        result1.add("pois", resultArray);
        System.out.println("API Hit");
        return getDumyjson();
    }

    private void reviewAndImage(JsonObject poiJson, JsonArray jsonElements) {
        JsonArray result = poiJson.getAsJsonArray("results");
        for (int i = 0; i < result.size(); i++) {
            JsonObject finalJsonObject = new JsonObject();
            JsonObject jsonObject = result.get(i).getAsJsonObject();
            finalJsonObject.add("poi", jsonObject.get("poi").getAsJsonObject());
            finalJsonObject.add("position", jsonObject.get("position").getAsJsonObject());
            finalJsonObject.add("entryPoints", jsonObject.get("entryPoints").getAsJsonArray().get(0).getAsJsonObject().get("position"));
            finalJsonObject.addProperty("wikitext", addWikiText(jsonObject));
            String url = RouteUtils.counstructReviewUlr(jsonObject.getAsJsonObject().get("poi"), jsonObject.getAsJsonObject().get("position"));
            String response = HttpConnection.hitHTTPConnection(url);
            if (response == null) {
                return;
            }
            JsonObject jsonObject1 = new Gson().fromJson(response, JsonObject.class);
//            jsonObject.add("ratingAndReview", jsonObject1);
            finalJsonObject.add("ratingAndReview", jsonObject1);
            jsonElements.add(finalJsonObject);
        }
    }

    @GetMapping(path = "convert")
    public void validateUser(final String poiNameText) throws IOException {
        final StringBuilder sb = new StringBuilder()
                .append("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=true&explaintext=true&titles=");
        sb.append(poiNameText);
        final URL obj = new URL(sb.toString());
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        final int responseCode = con.getResponseCode();

        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        final String textToSpeak = getTextToSpeak(response.toString());
        new SpeechUtils(textToSpeak).speak();
    }

    public String addWikiText(JsonObject poiJson) {
        StringBuffer response = null;
        try {
            final StringBuilder sb = new StringBuilder().append("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=true&explaintext=true&titles=");
            String name = poiJson.get("poi").getAsJsonObject().get("name").getAsString().replaceAll("\"", "");
            sb.append(name);

            final URL obj = new URL(sb.toString());
            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            return "Sorry No Information available";
        }
        return getTextToSpeak(response.toString());

        // new SpeechUtils(textToSpeak).speak();
    }


    private String getLimitedLines(final String tag) {
        final StringBuilder data = new StringBuilder();
        int statementCount = 0;
        for (final Character c : tag.toCharArray()) {
            if (statementCount > 3) {
                break;
            } else if (c == '.') {
                statementCount++;
            }
            data.append(c);
        }
        return data.toString();
    }

    private String getTextToSpeak(String json) {
        String text = "Sorry No Information available";
        final Gson gson = new Gson();
        final JsonObject poiJson = gson.fromJson(json, JsonObject.class);
        if (!poiJson.toString().contains("\"extract\":\"\"")) {
            final JsonObject result = poiJson.getAsJsonObject();
            final JsonElement jsonElement = result.get("query").getAsJsonObject().get("pages");
            Map<String, Object> map = new HashMap<>();
            map = gson.fromJson(jsonElement, map.getClass());
            final Map.Entry<String, Object> next = map.entrySet().iterator().next();
            final String tag = next.getValue().toString();
            text = getLimitedLines(tag.substring(tag.indexOf("extract") + 8));
            /*
             * if (tag.length() > 200) { text = tag.substring(tag.indexOf("extract") + 8, 200); } else { text =
             * tag.substring(tag.indexOf("extract") + 8); }
             */
        }
        return text;
    }


    public String getDumyjson() {
        return "{\n" +
                "\t\"pois\": [{\n" +
                "\t\t\"poi\": {\n" +
                "\t\t\t\"name\": \"CD Team\",\n" +
                "\t\t\t\"phone\": \"+(44)-(20)-77994044\",\n" +
                "\t\t\t\"url\": \"www.fullers.co.uk\",\n" +
                "\t\t\t\"categories\": [\"bed breakfast guest houses\", \"hotel/motel\"],\n" +
                "\t\t\t\"classifications\": [{\n" +
                "\t\t\t\t\"code\": \"HOTEL_MOTEL\",\n" +
                "\t\t\t\t\"names\": [{\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"hotel/motel\"\n" +
                "\t\t\t\t}, {\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"bed breakfast guest houses\"\n" +
                "\t\t\t\t}]\n" +
                "\t\t\t}]\n" +
                "\t\t},\n" +
                "\t\t\"position\": {\n" +
                "\t\t\t\"lat\": 18.55579,\n" +
                "\t\t\t\"lon\": 73.89207\n" +
                "\t\t},\n" +
                "\t\t\"entryPoints\": {\n" +
                "\t\t\t\"lat\": 51.49972,\n" +
                "\t\t\t\"lon\": -0.13192\n" +
                "\t\t},\n" +
                "\t\t\"wikitext\": \"Sorry No Information available\",\n" +
                "\t\t\"ratingAndReview\": {\n" +
                "\t\t\t\"candidates\": [{\n" +
                "\t\t\t\t\"formatted_address\": \"33 Tothill St, Westminster, London SW1H 9LA, UK\",\n" +
                "\t\t\t\t\"name\": \"The Sanctuary House Hotel\",\n" +
                "\t\t\t\t\"opening_hours\": {\n" +
                "\t\t\t\t\t\"open_now\": true\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"photos\": [{\n" +
                "\t\t\t\t\t\"height\": 1280,\n" +
                "\t\t\t\t\t\"html_attributions\": [\"The Sanctuary House Hotel\"],\n" +
                "\t\t\t\t\t\"photo_reference\": \"CmRaAAAA5uVpryd5-kBVkEwuD3JSj_h7DqZfyl_BW8-f5nWbmdhHlU4LwvQsDe2327_8kVwQS9qktRnogVJwgmkwH_kle4VDnrnKKPWYidajROmj3beXAJgpohIxtwLBdBAk3v6ZEhDZtPyE6rzyg5Qy8Y_X0qaMGhRThelzfbAnSKr10F2Mk7pBh_Rs-w\",\n" +
                "\t\t\t\t\t\"width\": 1920\n" +
                "\t\t\t\t}],\n" +
                "\t\t\t\t\"rating\": 4.2\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"debug_log\": {\n" +
                "\t\t\t\t\"line\": []\n" +
                "\t\t\t},\n" +
                "\t\t\t\"status\": \"OK\"\n" +
                "\t\t}\n" +
                "\t}, {\n" +
                "\t\t\"poi\": {\n" +
                "\t\t\t\"name\": \"Creaticity Mall\",\n" +
                "\t\t\t\"phone\": \"+(44)-(20)-33018080\",\n" +
                "\t\t\t\"url\": \"www.hiltonhotels.com/\",\n" +
                "\t\t\t\"categories\": [\"hotel\", \"hotel/motel\"],\n" +
                "\t\t\t\"classifications\": [{\n" +
                "\t\t\t\t\"code\": \"HOTEL_MOTEL\",\n" +
                "\t\t\t\t\"names\": [{\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"hotel/motel\"\n" +
                "\t\t\t\t}, {\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"hotel\"\n" +
                "\t\t\t\t}]\n" +
                "\t\t\t}]\n" +
                "\t\t},\n" +
                "\t\t\"position\": {\n" +
                "\t\t\t\"lat\": 18.5564774,\n" +
                "\t\t\t\"lon\": 73.8941453\n" +
                "\t\t},\n" +
                "\t\t\"entryPoints\": {\n" +
                "\t\t\t\"lat\": 51.49958,\n" +
                "\t\t\t\"lon\": -0.133\n" +
                "\t\t},\n" +
                "\t\t\"wikitext\": \"Sorry No Information available\",\n" +
                "\t\t\"ratingAndReview\": {\n" +
                "\t\t\t\"candidates\": [{\n" +
                "\t\t\t\t\"formatted_address\": \"22-28 Broadway, Westminster, London SW1H 0BH, UK\",\n" +
                "\t\t\t\t\"name\": \"Conrad London St. James\",\n" +
                "\t\t\t\t\"photos\": [{\n" +
                "\t\t\t\t\t\"height\": 1367,\n" +
                "\t\t\t\t\t\"html_attributions\": [\"Conrad London St. James\"],\n" +
                "\t\t\t\t\t\"photo_reference\": \"CmRaAAAA0xbqx5Fu01wZcx6SlbPmb9R5LZ-UlZtaa_Jv-TqXhjM1bY-krkdt1mIgOtaT2szxDC1oSUKBSy38A0vLS4FEcCeal19UEycupAJ30Fm8eQ_VxDQTvIUKm_Zq80jVs-asEhA5pwjo2jspA-PAsCVLlzHWGhQm6nbHK6cS0fH49LcfwcjrdQC4XQ\",\n" +
                "\t\t\t\t\t\"width\": 2048\n" +
                "\t\t\t\t}],\n" +
                "\t\t\t\t\"rating\": 4.5\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"debug_log\": {\n" +
                "\t\t\t\t\"line\": []\n" +
                "\t\t\t},\n" +
                "\t\t\t\"status\": \"OK\"\n" +
                "\t\t}\n" +
                "\t}, {\n" +
                "\t\t\"poi\": {\n" +
                "\t\t\t\"name\": \"Xerox Machine\",\n" +
                "\t\t\t\"categories\": [\"company\", \"services\"],\n" +
                "\t\t\t\"classifications\": [{\n" +
                "\t\t\t\t\"code\": \"COMPANY\",\n" +
                "\t\t\t\t\"names\": [{\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"company\"\n" +
                "\t\t\t\t}, {\n" +
                "\t\t\t\t\t\"nameLocale\": \"en-US\",\n" +
                "\t\t\t\t\t\"name\": \"services\"\n" +
                "\t\t\t\t}]\n" +
                "\t\t\t}]\n" +
                "\t\t},\n" +
                "\t\t\"position\": {\n" +
                "\t\t\t\"lat\": 18.556112,\n" +
                "\t\t\t\"lon\": 73.89178\n" +
                "\t\t},\n" +
                "\t\t\"entryPoints\": {\n" +
                "\t\t\t\"lat\": 51.49966,\n" +
                "\t\t\t\"lon\": -0.13295\n" +
                "\t\t},\n" +
                "\t\t\"wikitext\": \"Sorry No Information available\",\n" +
                "\t\t\"ratingAndReview\": {\n" +
                "\t\t\t\"candidates\": [{\n" +
                "\t\t\t\t\"formatted_address\": \"Ship Ln, Aveley, Purfleet RM19 1YN, UK\",\n" +
                "\t\t\t\t\"name\": \"The Thurrock Hotel\",\n" +
                "\t\t\t\t\"opening_hours\": {\n" +
                "\t\t\t\t\t\"open_now\": true\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"photos\": [{\n" +
                "\t\t\t\t\t\"height\": 1998,\n" +
                "\t\t\t\t\t\"html_attributions\": [\"The Thurrock Hotel\"],\n" +
                "\t\t\t\t\t\"photo_reference\": \"CmRaAAAAjPCqG5ZGj80PN0GnlULoPFQFBksZtKgVV_CEgoq1CmSalVA_owegpdJODGZNo44GUwTau2ooqxjSAa-Ljj6E0Jxs9rPPs6ChllDcAx-9WgEXE9Vx6B3Mln8U9_yBtIZuEhAJFFRfRp5TaFNTIPE-DOCUGhRza9jNtAMHUFZywgAu5XIfAh89iQ\",\n" +
                "\t\t\t\t\t\"width\": 4011\n" +
                "\t\t\t\t}],\n" +
                "\t\t\t\t\"rating\": 3.4\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"debug_log\": {\n" +
                "\t\t\t\t\"line\": []\n" +
                "\t\t\t},\n" +
                "\t\t\t\"status\": \"OK\"\n" +
                "\t\t}\n" +
                "\t}]\n" +
                "}";
    }

}












