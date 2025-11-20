package db;

import lombok.Getter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Getter
public class ConfiguracionSistema {
    private static final Logger logger = Logger.getLogger(ConfiguracionSistema.class);
    private static ConfiguracionSistema instance;
    private Connection connection;

    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbDriver;

    private ConfiguracionSistema() {
        logger.info("Iniciando configuración del sistema...");
        cargarConfiguracion();
        logger.info("Configuración del sistema cargada correctamente.");
    }

    public static ConfiguracionSistema getInstance() {
        if(instance == null) {
            logger.info("Creando instancia de ConfiguracionSistema...");
            instance = new ConfiguracionSistema();
        }
        return instance;
    }

    // Devuelve una conexión válida, creando una nueva si es necesario
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            logger.info("Conexión establecida con la base de datos.");
        }
        return connection;
    }

    // Carga de propiedades desde config.properties
    private void cargarConfiguracion() {
        Properties props = new Properties();
        try(InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if (input == null) {
                throw new RuntimeException( "No se encontró el archivo config.properties");
            }
            props.load(input);
            this.dbUrl = props.getProperty("db.url");
            this.dbUser = props.getProperty("db.user");
            this.dbPassword = props.getProperty("db.password");
            this.dbDriver = props.getProperty("db.driver", "org.postgresql.Driver");

            if (dbUrl == null || dbUser == null || dbPassword == null) {
                throw new RuntimeException("Propiedades de base de datos faltantes en config.properties");
            }
            logger.info("Propiedades de base de datos cargadas exitosamente desde config.properties.");
        } catch (IOException e) {
            logger.error("Error al cargar la configuración del sistema", e);
            throw new RuntimeException("Error al cargar la configuración del sistema", e);
        }
    }
}
