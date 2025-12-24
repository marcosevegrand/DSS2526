package dss2526.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConfig {

    private static DBConfig instance;
    private Properties properties;

    private DBConfig() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find db.properties, using defaults or env vars.");
                // Fallback or throw exception
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            // Load driver if needed (modern JDBC drivers often auto-load, but good for safety)
            String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"); 
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DBConfig getInstance() {
        if (instance == null) {
            instance = new DBConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url", "jdbc:mysql://localhost:3306/restaurante_db?serverTimezone=Europe/Lisbon");
        String user = properties.getProperty("db.user", "root");
        String password = properties.getProperty("db.password", "root");
        
        return DriverManager.getConnection(url, user, password);
    }
}