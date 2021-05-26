package su.sold.playersgeo.util;


import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import su.sold.playersgeo.Plugin;

import java.io.IOException;

public class Geo {
    public static boolean isLocalIP(String address){
        return false;
    }
    public static JSONObject getGeoIPData(String address) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://www.geoplugin.net/json.gp?ip=" + address);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    return null;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {

                    String result = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(result);
                    if(json.getInt("geoplugin_status")!=200){
                        return null;
                    }else{
                        return json;
                    }
                }else{
                    Plugin.log.severe("[PlayersGeo] www.geoplugin.net returned an empty response!");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
