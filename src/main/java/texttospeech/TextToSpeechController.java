package texttospeech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@RestController
@RequestMapping
public class TextToSpeechController {

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
        String text = "No data, available";
        final Gson gson = new Gson();
        final JsonObject poiJson = gson.fromJson(json, JsonObject.class);
        if (!poiJson.toString().contains("\"extract\":\"\"")) {
            final JsonObject result = poiJson.getAsJsonObject();
            final JsonElement jsonElement = result.get("query").getAsJsonObject().get("pages");
            Map<String, Object> map = new HashMap<>();
            map = gson.fromJson(jsonElement, map.getClass());
            final Entry<String, Object> next = map.entrySet().iterator().next();
            final String tag = next.getValue().toString();
            text = getLimitedLines(tag.substring(tag.indexOf("extract") + 8));
            /*
             * if (tag.length() > 200) { text = tag.substring(tag.indexOf("extract") + 8, 200); } else { text =
             * tag.substring(tag.indexOf("extract") + 8); }
             */
        }
        System.out.println("returning text : " + text);
        return text;
    }


}
