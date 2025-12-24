package dss2526.data.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestor de configuração da base de dados.
 * Lê as credenciais a partir de um ficheiro externo (db.properties).
 */
public class DBConfig {

    private static final String PROPERTIES_FILE = "db.properties";
    private static final Properties props = new Properties();

    static {
        try (
                InputStream is = DBConfig.class.getClassLoader().getResourceAsStream(
                        PROPERTIES_FILE)) {
            if (is == null) {
                throw new RuntimeException(
                        "Ficheiro " +
                                PROPERTIES_FILE +
                                " não encontrado na pasta resources.");
            }
            props.load(is);

            // Workaround: Sanitize keys (fix BOM/Encoding issues)
            java.util.Enumeration<?> keys = props.propertyNames();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (key.contains("db.url") && !key.equals("db.url")) {
                    String value = props.getProperty(key);
                    props.setProperty("db.url", value);
                }
                if (key.contains("db.user") && !key.equals("db.user")) {
                    props.setProperty("db.user", props.getProperty(key));
                }
            }

            // Debug: Validate keys
            if (props.getProperty("db.url") == null) {
                System.err.println("DBConfig Error: 'db.url' not found in db.properties!");
                System.err.println("Loaded keys: " + props.keySet());
                throw new RuntimeException("Chave 'db.url' em falta no ficheiro db.properties");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                    "Erro ao carregar configurações da base de dados.",
                    e);
        }
    }

    private DBConfig() {
    }

    /**
     * Obtém uma nova conexão à base de dados usando as propriedades carregadas.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
    }
}
