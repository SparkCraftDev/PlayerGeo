package su.sold.playersgeo.listener;


import fr.xephi.authme.events.LoginEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import su.sold.playersgeo.Database;
import su.sold.playersgeo.Plugin;
import su.sold.playersgeo.util.Geo;
import java.io.IOException;
import java.util.Objects;

public class AuthMeListener implements Listener {
    private Plugin plugin;
    public AuthMeListener(Plugin p){
        plugin = p;
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onLogin(LoginEvent event) throws IOException {
        final Player ply = event.getPlayer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = null;
                try {
                    data = Geo.getGeoIPData(Objects.requireNonNull(ply.getAddress()).getAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(data!=null) {
                    Plugin.log.info("§c[§ePlayers§6Geo§c] §f"+ply.getName()+ " §e from §l"+data.getString("geoplugin_city")+", "+data.getString("geoplugin_countryCode"));
                    Geo.notifyOnPlayerJoin(ply.getName()+ "§e from §l"+data.getString("geoplugin_city")+", "+data.getString("geoplugin_countryCode"));
                    plugin.db.add(ply.getName(), data.getString("geoplugin_city"), data.getString("geoplugin_countryName"), data.getString("geoplugin_countryCode"), data.getString("geoplugin_latitude"), data.getString("geoplugin_longitude"));
                }else{
                    Plugin.log.info("§c[§ePlayers§6Geo§c] §cGeo data not found for §e" +ply.getName());
                }

            }
        }).start();

    }

}

