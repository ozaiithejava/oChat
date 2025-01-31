package net.ozaii.oChat.managers;

import net.ozaii.oChat.OChat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class PlayerChatManager {
    private static PlayerChatManager instance;
    private final DatabaseManager databaseManager;
    private final ConfigManager configManager;
    private final Logger logger;
    private final String tableName;

    private PlayerChatManager(DatabaseManager databaseManager, ConfigManager configManager) {
        this.databaseManager = databaseManager;
        this.configManager = configManager;
        this.logger = OChat.getInstance().getLogger();

        this.tableName = configManager.getString("config.yml", "database.player_messages_table", "player_messages");

        createTable();
    }

    public static synchronized PlayerChatManager getInstance(DatabaseManager dbManager, ConfigManager configManager) {
        if (instance == null) {
            instance = new PlayerChatManager(dbManager, configManager);
        }
        return instance;
    }

    private void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "player_name VARCHAR(50) UNIQUE, " +
                "message_count INT DEFAULT 0" +
                ");";

        databaseManager.getConnection().thenAccept(connection -> {
            if (connection != null) {
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.executeUpdate();
                    logger.info("Tablo oluşturuldu veya zaten mevcut: " + tableName);
                } catch (SQLException e) {
                    logger.severe("Tablo oluşturulurken hata oluştu: " + e.getMessage());
                } finally {
                    try {
                        connection.close();
                    } catch (SQLException ignored) {}
                }
            }
        });
    }

    public CompletableFuture<Void> addPlayerMessage(int count, String playerName) {
        return CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO " + tableName + " (player_name, message_count) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE message_count = message_count + ?";

            try (Connection connection = databaseManager.getConnection().get();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setString(1, playerName);
                stmt.setInt(2, count);
                stmt.setInt(3, count);
                stmt.executeUpdate();
                logger.info(playerName + " için mesaj sayısı güncellendi.");

            } catch (SQLException | InterruptedException | ExecutionException e) {
                logger.severe("Mesaj eklenirken hata oluştu: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Integer> getPlayerMessages(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            String query = "SELECT message_count FROM " + tableName + " WHERE player_name = ?";
            int messageCount = 0;

            try (Connection connection = databaseManager.getConnection().get();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.setString(1, playerName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    messageCount = rs.getInt("message_count");
                }

            } catch (SQLException | InterruptedException | ExecutionException e) {
                logger.severe("Mesaj sayısı alınırken hata oluştu: " + e.getMessage());
            }
            return messageCount;
        });
    }

    public CompletableFuture<Void> resetAllMessages() {
        return CompletableFuture.runAsync(() -> {
            String query = "UPDATE " + tableName + " SET message_count = 0";

            try (Connection connection = databaseManager.getConnection().get();
                 PreparedStatement stmt = connection.prepareStatement(query)) {

                stmt.executeUpdate();
                logger.info("Tüm mesaj sayıları sıfırlandı.");

            } catch (SQLException | InterruptedException | ExecutionException e) {
                logger.severe("Tüm mesajlar sıfırlanırken hata oluştu: " + e.getMessage());
            }
        });
    }
}
