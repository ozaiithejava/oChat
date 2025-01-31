package net.ozaii.oChat.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigManager {
    private static ConfigManager instance;
    private final JavaPlugin plugin;
    private final Logger logger;
    private final Map<String, FileConfiguration> configMap = new HashMap<>();
    private final Map<String, File> configFiles = new HashMap<>();

    private ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public static ConfigManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new ConfigManager(plugin);
        }
        return instance;
    }

    public void loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        configMap.put(fileName, config);
        configFiles.put(fileName, file);
        logger.info(fileName + " başarıyla yüklendi.");
    }

    public boolean getBoolean(String fileName, String path, boolean def) {
        return configMap.containsKey(fileName) ? configMap.get(fileName).getBoolean(path, def) : def;
    }

    public int getInt(String fileName, String path, int def) {
        return configMap.containsKey(fileName) ? configMap.get(fileName).getInt(path, def) : def;
    }

    public String getString(String fileName, String path, String def) {
        return configMap.containsKey(fileName) ? configMap.get(fileName).getString(path, def) : def;
    }

    public double getDouble(String fileName, String path, double def) {
        return configMap.containsKey(fileName) ? configMap.get(fileName).getDouble(path, def) : def;
    }

    public List<String> getList(String fileName, String path) {
        if (configMap.containsKey(fileName)) {
            return configMap.get(fileName).getStringList(path);
        }
        return null;
    }

    public LinkedList<String> getLinkedList(String fileName, String path) {
        LinkedList<String> linkedList = new LinkedList<>();
        if (configMap.containsKey(fileName)) {
            List<String> list = configMap.get(fileName).getStringList(path);
            linkedList.addAll(list);
        }
        return linkedList;
    }

    public void saveConfig(String fileName) {
        if (!configMap.containsKey(fileName)) return;
        try {
            configMap.get(fileName).save(configFiles.get(fileName));
            logger.info(fileName + " kaydedildi.");
        } catch (IOException e) {
            logger.severe("Hata! " + fileName + " kaydedilemedi: " + e.getMessage());
        }
    }

    public void reloadConfig(String fileName) {
        if (!configFiles.containsKey(fileName)) return;
        configMap.put(fileName, YamlConfiguration.loadConfiguration(configFiles.get(fileName)));
        logger.info(fileName + " yeniden yüklendi.");
    }

    public void closeConfig() {
        configMap.clear();
        configFiles.clear();
        logger.info("Tüm konfigürasyon dosyaları kapatıldı.");
    }
}
