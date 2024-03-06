package su.sold.playersgeo.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.IOException;

public class Geo {
    private Geo() {
    }

    public static JSONObject getGeoIPData(String address) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://www.geoplugin.net/json.gp?ip=" + address);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() != 200) {
                    return null;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("geoplugin_status") != 200) {
                        return null;
                    } else {
                        return json;
                    }
                } else {
                    Bukkit.getLogger().severe("www.geoplugin.net returned an empty response!");
                    return null;
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to execute HTTP request to geoplugin.net: " + e.getMessage());
            return null;
        } catch (ParseException e) {
            Bukkit.getLogger().severe("Failed to parse JSON response from geoplugin.net: " + e.getMessage());
            return null;
        }
    }
}
