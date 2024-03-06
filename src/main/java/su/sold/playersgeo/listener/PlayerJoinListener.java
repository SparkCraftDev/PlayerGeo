package su.sold.playersgeo.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import su.sold.playersgeo.Database;
import su.sold.playersgeo.util.Geo;

import java.util.Objects;

public class PlayerJoinListener implements Listener {
    private static final String PERMISSION = "playersgeo.notifyonjoin";
    private final JavaPlugin plugin;
    private final Database database;

    public PlayerJoinListener(JavaPlugin plugin, Database database) {
        this.plugin = plugin;
        this.database = database;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            JSONObject data = Geo.getGeoIPData(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
            if (data != null) {
                String city = data.getString("geoplugin_city");
                String countryName = data.getString("geoplugin_countryName");
                String countryCode = data.getString("geoplugin_countryCode");
                String latitude = data.getString("geoplugin_latitude");
                String longitude = data.getString("geoplugin_longitude");

                Component message = Component.text(player.getName())
                        .append(Component
                                .text(" from ")
                                .color(NamedTextColor.YELLOW))
                        .append(Component
                                .text(countryCode));

                notifyOnPlayerJoin(message);

                Bukkit.getConsoleSender().sendMessage(getPrefix()
                        .append(Component
                                .text(player.getName()))
                        .append(Component
                                .text(" from ")
                                .color(NamedTextColor.YELLOW))
                        .append(Component
                                .text(city + ", " + countryCode)));

                database.add(player.getName(), city, countryName, countryCode, latitude, longitude);
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix()
                        .append(Component
                                .text("Geo data not found for ")
                                .color(NamedTextColor.RED))
                        .append(Component
                                .text(player.getName())
                                .color(NamedTextColor.YELLOW)));
            }
        });
    }

    private void notifyOnPlayerJoin(Component message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(PERMISSION)) {
                player.sendMessage(getPrefix().append(message));
            }
        }
    }

    private Component getPrefix() {
        return Component.text("[")
                .color(NamedTextColor.RED)
                .append(Component
                        .text("Players")
                        .color(NamedTextColor.YELLOW))
                .append(Component
                        .text("Geo")
                        .color(NamedTextColor.GOLD))
                .append(Component
                        .text("] ")
                        .color(NamedTextColor.RED));
    }
}
