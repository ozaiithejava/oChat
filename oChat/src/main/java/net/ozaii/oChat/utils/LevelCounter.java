package net.ozaii.oChat.utils;

import net.milkbowl.vault.economy.Economy;
import net.ozaii.oChat.OChat;
import net.ozaii.oChat.managers.ConfigManager;
import net.ozaii.oChat.managers.PlayerChatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LevelCounter {

    private static final PlayerChatManager manager = OChat.getInstance().getPlayerChatManager();
    private static final ConfigManager configManager = OChat.getInstance().getConfigManager();
    private static final Economy economy = OChat.getInstance().getEconomy(); //vault

    public static int getPlayerLevel(String playerName) {
        return manager.getPlayerMessages(playerName).join();
    }

    public static void givePlayerRewards(String playerName) {
        int playerLevel = getPlayerLevel(playerName);
        if (playerLevel <= 0) return;

        int rewardCoins = configManager.getInt("config.yml", "reawards.levels." + playerLevel, 0);

        if (rewardCoins <= 0) {
            Bukkit.getLogger().warning("[oChat] Ödül bulunamadı veya 0 değerinde: Level " + playerLevel);
            return;
        }

        Player player = Bukkit.getPlayer(playerName);
        if (player == null || !player.isOnline()) return;

        if (economy != null) {
            economy.depositPlayer(player, rewardCoins);
            String msg = MessageUtils.getRewardsMessage(playerName, getPlayerLevel(playerName),rewardCoins);
            player.sendMessage(msg);
        } else {
            Bukkit.getLogger().warning("[oChat] Vault bulunamadı, ödül verilemedi!");
        }
    }
}
