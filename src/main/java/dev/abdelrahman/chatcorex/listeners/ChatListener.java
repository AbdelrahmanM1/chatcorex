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

        // Staff chat toggle → skip global chat
        if (StaffChatCommand.isStaffChatToggled(player)) {
            return;
        }

        // Chat mute check
        if (plugin.isChatMuted() && !player.hasPermission("chatcorex.bypassmute")) {
            player.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("chat.muted")));
            event.setCancelled(true);
            return;
        }

        // Chat cooldown check
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

        // Filter swears
        String filteredMessage = MessageUtils.filterSwear(plugin, player, event.getMessage());

        // Cancel vanilla format
        event.setCancelled(true);

        // Base format from config.yml - apply placeholders FIRST
        String baseFormat = plugin.getConfig().getString("chat.format", "&7%rankcorex_prefix%%player_name%: %message%");

        // Replace basic placeholders first
        baseFormat = baseFormat.replace("%player_name%", player.getName())
                .replace("%message%", filteredMessage);

        // Apply PlaceholderAPI placeholders (including rankcorex placeholders)
        baseFormat = PlaceholderUtils.setPlaceholders(player, baseFormat);

        // Send to all recipients
        for (Player recipient : Bukkit.getOnlinePlayers()) {
            String messageToSend = baseFormat;

            // Mentions highlight
            if (filteredMessage.toLowerCase().contains(recipient.getName().toLowerCase())) {
                String highlighted = filteredMessage.replaceAll(
                        "(?i)" + Pattern.quote(recipient.getName()),
                        ChatColor.YELLOW + recipient.getName() + ChatColor.RESET
                );

                // Recreate message with highlighted mention
                String mentionFormat = plugin.getConfig().getString("chat.format", "&7%rankcorex_prefix%%player_name%: %message%");
                mentionFormat = mentionFormat.replace("%player_name%", player.getName())
                        .replace("%message%", highlighted);

                // Apply PlaceholderAPI placeholders for mention message
                messageToSend = PlaceholderUtils.setPlaceholders(player, mentionFormat);

                // Play ping sound
                try {
                    recipient.playSound(recipient.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1f, 1f);
                } catch (IllegalArgumentException e) {
                    try {
                        recipient.playSound(recipient.getLocation(), Sound.valueOf("LEVEL_UP"), 1f, 1f);
                    } catch (IllegalArgumentException e2) {
                        // Fallback for older versions
                        plugin.getLogger().warning("Could not play mention sound - unsupported sound type");
                    }
                }
            }

            recipient.sendMessage(MessageUtils.colorize(messageToSend));
        }
    }
}