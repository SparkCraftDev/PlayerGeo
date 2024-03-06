package su.sold.playersgeo;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import su.sold.playersgeo.listener.PlayerJoinListener;

public final class Plugin extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        FileConfiguration cfg = getConfig();
        this.database = Database.getInstance(cfg);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this, database), this);
        PluginCommand geoCommand = getCommand("geo");
        assert geoCommand != null;
        geoCommand.setExecutor(new GeoCommand(database));
    }

    @Override
    public void onDisable() {
        database.closeConnection();
        Bukkit.getLogger().info("Disabling PlayersGeo...");
    }
}
