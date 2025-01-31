package net.ozaii.oChat.managers;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Logger logger;
    private HikariDataSource dataSource;

    private DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = ConfigManager.getInstance(plugin);
        this.logger = plugin.getLogger();
        setupDatabase();
    }

    public static synchronized DatabaseManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new DatabaseManager(plugin);
        }
        return instance;
    }

    private void setupDatabase() {
        String fileName = "config.yml";

        String host = configManager.getString(fileName, "database.host", "localhost");
        int port = configManager.getInt(fileName, "database.port", 3306);
        String database = configManager.getString(fileName, "database.name", "minecraft");
        String user = configManager.getString(fileName, "database.username", "root");
        String password = configManager.getString(fileName, "database.password", "");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(60000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setConnectionTimeout(30000);

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Database bağlantısı başarıyla kuruldu!");
    }

    public CompletableFuture<Connection> getConnection() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                logger.severe("Veritabanına bağlanırken hata oluştu: " + e.getMessage());
                return null;
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database bağlantısı kapatıldı.");
        }
    }
}
