package su.sold.playersgeo;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static Database database;
    private final JavaPlugin plugin;
    private final FileConfiguration cfg;

    public Database(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        if(connect()){
            createDatabases();
        }else{
            Plugin.log.severe("[PlayersGeo] Couldn't connect to database. Check database credentials in config.yml");
            plugin.onDisable();
        }
    }

    private Connection connection;


    private boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }

            synchronized (this) {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(buildLink(), getLogin(), getPassword());
                if (!connection.isValid(5)) {
                    return connect();
                }
                return true;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    void createDatabases() {
        try {

            String sql = "CREATE TABLE IF NOT EXISTS `PlayersGeo` (`username` TEXT NOT NULL, `city` TEXT NULL, `country_name` TEXT NULL, `country_code` TEXT NULL, `coordinates` POINT NULL, `showonmap` TINYINT NOT NULL DEFAULT 0, UNIQUE INDEX `username_UNIQUE` (`username` ASC));";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isConnectionValid() {
        try {
            boolean isConnectionValid = connection.isValid(5);
            if (!isConnectionValid) {
                return connect();
            } else {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String buildLink() {

        return "jdbc:mysql://" + cfg.getString("database_host") + ":" + cfg.getString("database_port") + "/" + cfg.getString("database_name");
    }

    private String getLogin() {
        return cfg.getString("database_login");
    }

    private String getPassword() {
        return cfg.getString("database_password");

    }
    public boolean add(String username, String city, String country_name, String country_code, String latitude, String longitude){
        try{
            String sql = "INSERT INTO `PlayersGeo` (`username`, `city`, `country_name`, `country_code`, `coordinates`) VALUES (?, ?, ?, ?, POINT(?, ?)) ON DUPLICATE KEY UPDATE `city`=?, `country_name`=?, `country_code`=?, `coordinates`=POINT(?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, city);
            preparedStatement.setString(3, country_name);
            preparedStatement.setString(4, country_code);
            preparedStatement.setString(5, longitude);
            preparedStatement.setString(6, latitude);

            //duplicate
            preparedStatement.setString(7, city);
            preparedStatement.setString(8, country_name);
            preparedStatement.setString(9, country_code);
            preparedStatement.setString(10, longitude);
            preparedStatement.setString(11, latitude);


            preparedStatement.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }
    public List<String> get(String username){
        List<String> result = new ArrayList<String>();
        try{
            String sql = "SELECT * FROM `PlayersGeo` WHERE `username` = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet data= preparedStatement.executeQuery();
            if(data.next()) {
                result.add(data.getString(2));
                result.add(data.getString(3));
                result.add(data.getString(4));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return result;
    }
}