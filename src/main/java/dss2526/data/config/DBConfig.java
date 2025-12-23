package pt.uminho.dss.restaurante.persistence.config;

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
            InputStream is =
                DBConfig.class.getClassLoader().getResourceAsStream(
                    PROPERTIES_FILE
                )
        ) {
            if (is == null) {
                throw new RuntimeException(
                    "Ficheiro " +
                        PROPERTIES_FILE +
                        " não encontrado na pasta resources."
                );
            }
            props.load(is);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                "Erro ao carregar configurações da base de dados.",
                e
            );
        }
    }

    private DBConfig() {}

    /**
     * Obtém uma nova conexão à base de dados usando as propriedades carregadas.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}
