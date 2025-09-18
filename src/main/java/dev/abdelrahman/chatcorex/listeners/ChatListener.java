package dev.abdelrahman.chatcorex.listeners;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChatListener implements Listener {

    private final Chatcorex plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public ChatListener(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Chat muted
        if (plugin.isChatMuted() && !player.hasPermission("chatcorex.bypassmute")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("chat.muted")));
            event.setCancelled(true);
            return;
        }

        // Chat cooldown
        int cooldownTime = plugin.getConfig().getInt("chat.cooldown-time", 3);
        long now = System.currentTimeMillis();
        if (!player.hasPermission("chatcorex.bypasscooldown")) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                long lastMessage = cooldowns.get(player.getUniqueId());
                if ((now - lastMessage) < (cooldownTime * 1000L)) {
                    long remaining = ((cooldownTime * 1000L) - (now - lastMessage)) / 1000L;
                    String msg = plugin.getConfig().getString("chat.cooldown")
                            .replace("%seconds%", String.valueOf(remaining));
                    player.sendMessage(MessageUtils.colorize(msg));
                    event.setCancelled(true);
                    return;
                }
            }
            cooldowns.put(player.getUniqueId(), now);
        }

        // Apply anti-swear
        String filteredMessage = MessageUtils.filterSwear(plugin, player, event.getMessage());
        event.setMessage(filteredMessage);

        // Fake rank system
        String rank = player.hasPermission("chatcorex.admin") ? "&cAdmin" : "&aMember";

        // Chat format
        String format = plugin.getConfig().getString("chat.format")
                .replace("%player_rank%", rank)
                .replace("%player_name%", player.getName())
                .replace("%message%", filteredMessage);

        event.setFormat(MessageUtils.colorize(format));
    }
}
