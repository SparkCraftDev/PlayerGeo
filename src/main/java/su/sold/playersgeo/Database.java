package su.sold.playersgeo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database instance;
    private final HikariDataSource dataSource;
    private final FileConfiguration cfg;

    private Database(FileConfiguration cfg) {
        this.cfg = cfg;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(buildLink());
        config.setUsername(cfg.getString("database_login"));
        config.setPassword(cfg.getString("database_password"));
        config.setMinimumIdle(5);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.dataSource = new HikariDataSource(config);

        createDatabases();
    }

    public static Database getInstance(FileConfiguration config) {
        if (instance == null) {
            instance = new Database(config);
        }
        return instance;
    }

    public void closeConnection() {
        dataSource.close();
    }

    private String buildLink() {
        return "jdbc:mysql://" + cfg.getString("database_host") + ":" + cfg.getString("database_port") + "/" + cfg.getString("database_name");
    }

    void createDatabases() {
        String sql = "CREATE TABLE IF NOT EXISTS `PlayersGeo` (`username` TEXT NOT NULL, `city` TEXT NULL, `country_name` TEXT NULL, `country_code` TEXT NULL, `coordinates` POINT NULL, `showonmap` TINYINT NOT NULL DEFAULT 0, UNIQUE INDEX `username_UNIQUE` (`username` ASC));";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    public void add(String username, String city, String countryName, String countryCode, String latitude, String longitude) {
        String sql = "INSERT INTO `PlayersGeo` (`username`, `city`, `country_name`, `country_code`, `coordinates`) VALUES (?, ?, ?, ?, POINT(?, ?)) ON DUPLICATE KEY UPDATE `city`=?, `country_name`=?, `country_code`=?, `coordinates`=POINT(?,?);";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, city);
            preparedStatement.setString(3, countryName);
            preparedStatement.setString(4, countryCode);
            preparedStatement.setString(5, longitude);
            preparedStatement.setString(6, latitude);

            //duplicate
            preparedStatement.setString(7, city);
            preparedStatement.setString(8, countryName);
            preparedStatement.setString(9, countryCode);
            preparedStatement.setString(10, longitude);
            preparedStatement.setString(11, latitude);

            preparedStatement.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
    }

    public List<String> get(String username) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT * FROM `PlayersGeo` WHERE `username` = ?;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet data = preparedStatement.executeQuery();

            if (data.next()) {
                result.add(data.getString(2));
                result.add(data.getString(3));
                result.add(data.getString(4));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe(e.getMessage());
        }
        return result;
    }
}