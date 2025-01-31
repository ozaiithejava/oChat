package net.ozaii.oChat.utils;

import net.ozaii.oChat.OChat;
import net.ozaii.oChat.managers.ConfigManager;
import org.bukkit.ChatColor;

public class MessageUtils {
    private static ConfigManager configManager = OChat.getInstance().getConfigManager();

    public static String getRewardsMessage(String playerName, int playerLevel, int rewardCoins) {
        String msg = configManager.getString("config.yml", "message.rewards", "");

        if (msg == null || msg.isEmpty()) {
            return ChatColor.RED + "Hata: Mesaj bulunamadı!";
        }

        msg = msg.replace("@p", playerName)
                .replace("@l", String.valueOf(playerLevel))
                .replace("@coins", String.valueOf(rewardCoins));

        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
