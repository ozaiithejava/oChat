package net.ozaii.oChat.utils;

import net.milkbowl.vault.economy.Economy;
import net.ozaii.oChat.OChat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconSetup {
    private static EconSetup instance;
    private static Economy econ = null;

    private EconSetup() {
    }

    public static void setup() {
        if (!setupEconomy()) {
            OChat plugin = OChat.getInstance();
            plugin.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", plugin.getDescription().getName()));
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private static boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
    public static EconSetup getInstance() {
        if (instance == null) {
            instance = new EconSetup();
        }
        return instance;
    }
}
