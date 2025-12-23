package pt.uminho.dss.fastfood.persistence.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConfig {

    static final String USERNAME = "root"; // Actualizar
    static final String PASSWORD = "root"; // Actualizar
    private static final String DATABASE = "restaurante_db"; // Actualizar
    private static final String DRIVER = "jdbc:mysql"; // Usar para MySQL
    static final String URL =
        DRIVER +
        "://localhost:3306/" +
        DATABASE +
        "?serverTimezone=Europe/Lisbon";

    // Opcional nas versões novas, mas não faz mal manter
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // driver MySQL 8.x
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver MySQL não encontrado no classpath.",
                e
            );
        }
    }

    private DBConfig() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
