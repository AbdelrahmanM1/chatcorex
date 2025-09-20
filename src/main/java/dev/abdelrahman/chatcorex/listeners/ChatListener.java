package dev.abdelrahman.chatcorex.listeners;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.commands.StaffChatCommand;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import dev.abdelrahman.chatcorex.utils.PlaceholderUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {

    private final Chatcorex plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public ChatListener(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // If player is in staff chat mode, ignore global chat
        if (StaffChatCommand.isStaffChatToggled(player)) {
            return;
        }

        // Chat muted
        if (plugin.isChatMuted() && !player.hasPermission("chatcorex.bypassmute")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("chat.muted")));
            event.setCancelled(true);
            return;
        }

        // Chat cooldown
        int cooldownTime = plugin.getConfig().getInt("chat.cooldown-time", 5);
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

        // Apply anti-swear filter
        String filteredMessage = MessageUtils.filterSwear(plugin, player, event.getMessage());

        // Get rank prefix (LuckPerms via PlaceholderAPI)
        String rank = PlaceholderUtils.setPlaceholders(player, "%luckperms_prefix%");
        if (rank == null) rank = "";

        // Cancel vanilla chat
        event.setCancelled(true);

        // Normal base format
        String baseFormat = plugin.getConfig().getString("chat.format")
                .replace("%rank%", rank)
                .replace("%player_name%", player.getName())
                .replace("%message%", filteredMessage);

        // Send to all players
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            String messageToSend = baseFormat;

            // If recipient is mentioned
            if (filteredMessage.toLowerCase().contains(recipient.getName().toLowerCase())) {
                String highlighted = filteredMessage.replaceAll(
                        "(?i)" + Pattern.quote(recipient.getName()),
                        ChatColor.YELLOW + recipient.getName() + ChatColor.RESET
                );

                messageToSend = plugin.getConfig().getString("chat.format")
                        .replace("%rank%", rank)
                        .replace("%player_name%", player.getName())
                        .replace("%message%", highlighted);

                // Play notification sound
                try {
                    recipient.playSound(recipient.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1f, 1f);
                } catch (IllegalArgumentException e) {
                    recipient.playSound(recipient.getLocation(), Sound.valueOf("LEVEL_UP"), 1f, 1f);
                }
            }

            recipient.sendMessage(MessageUtils.colorize(messageToSend));
        }

        // Log to console (with placeholders parsed)
        Bukkit.getConsoleSender().sendMessage(
                MessageUtils.colorize(
                        PlaceholderUtils.setPlaceholders(player, baseFormat)
                )
        );
    }
}
