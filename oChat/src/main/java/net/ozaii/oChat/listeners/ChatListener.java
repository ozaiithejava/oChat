package net.ozaii.oChat.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.ozaii.oChat.OChat;
import net.ozaii.oChat.managers.PlayerChatManager;
import net.ozaii.oChat.utils.LevelCounter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;

public class ChatListener implements Listener {
    private static PlayerChatManager chatManager = OChat.getInstance().getPlayerChatManager();

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        try {
            Player p = e.getPlayer();
            String playerName = p.getName();
            CompletableFuture<Integer> messageCount = chatManager.getPlayerMessages(playerName);
            chatManager.addPlayerMessage(1,playerName);
            LevelCounter.givePlayerRewards(playerName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
