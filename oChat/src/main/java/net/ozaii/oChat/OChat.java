package net.ozaii.oChat;

import net.milkbowl.vault.economy.Economy;
import net.ozaii.oChat.listeners.ChatListener;
import net.ozaii.oChat.managers.ConfigManager;
import net.ozaii.oChat.managers.DatabaseManager;
import net.ozaii.oChat.managers.PlayerChatManager;
import net.ozaii.oChat.utils.EconSetup;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class OChat extends JavaPlugin {

    private static OChat instance;
    private static ConfigManager configManager;
    private static Economy economy;
    private static EconSetup econSetup;
    private static DatabaseManager databaseManager;
    private static PlayerChatManager playerChatManager;
    @Override
    public void onEnable() {
        try {

         // instance
        instance = this;
        // configmanager singleton
        configManager = ConfigManager.getInstance(this);
        loadConfis();
        //setup vault
        econSetup = EconSetup.getInstance();
        econSetup.setup();
        economy = econSetup.getEconomy();
        //databasemanager singleton
        databaseManager = DatabaseManager.getInstance(this);
        //chatmanager instance
        playerChatManager = PlayerChatManager.getInstance(databaseManager,configManager);
        registerListeners();
        getLogger().info("oChat başarıyla yüklendi!");
        }catch (Exception e) {
            if (databaseManager == null) {
                getLogger().warning("DatabaseManager ayarlarını configten yapmadan çalışamam!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

    }
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
        if (configManager != null) {
            configManager.closeConfig();
        }
    }
    private static void loadConfis(){
        configManager.loadConfig("config.yml");
    }

    public static OChat getInstance() {if (instance == null) {instance = new OChat();}return instance;}
    public static ConfigManager getConfigManager() {return configManager;}
    public static EconSetup getEconSetup() {return econSetup;}
    public static Economy getEconomy() {return economy;}
    public static DatabaseManager getDatabaseManager() {return databaseManager;}
    public static PlayerChatManager getPlayerChatManager() {return playerChatManager;}

}
