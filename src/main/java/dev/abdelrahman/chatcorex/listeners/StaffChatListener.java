package dev.abdelrahman.chatcorex.listeners;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.commands.StaffChatCommand;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffChatListener implements Listener {

    private final Chatcorex plugin;

    public StaffChatListener(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String permission = plugin.getConfig().getString("staff-chat.permission", "chatcorex.staffchat");

        // Check if the player has staff chat toggled
        if (!StaffChatCommand.isStaffChatToggled(player)) {
            return;
        }

        // Check if staff chat is enabled in config
        if (!plugin.getConfig().getBoolean("staff-chat.enabled", true)) {
            player.sendMessage(ChatColor.RED + "Staff Chat is disabled in config.yml, please turn it on.");
            event.setCancelled(true);
            return;
        }

        // Cancel global chat
        event.setCancelled(true);

        String message = MessageUtils.filterSwear(plugin, player, event.getMessage());

        // Staff chat base format
        String baseFormat = plugin.getConfig().getString(
                "staff-chat.format",
                "&8[Staff] &b%player_name% &7» &f%message%"
        );

        // Send to online staff members
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission(permission)) {
                String formattedMessage = message;

                // Highlight for mentioned player
                if (message.toLowerCase().contains(online.getName().toLowerCase())) {
                    formattedMessage = message.replaceAll(
                            "(?i)" + online.getName(),
                            ChatColor.YELLOW + online.getName() + ChatColor.RESET
                    );

                    try {
                        online.playSound(online.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1f, 1f);
                    } catch (IllegalArgumentException e) {
                        online.playSound(online.getLocation(), Sound.valueOf("LEVEL_UP"), 1f, 1f);
                    }
                }

                String finalMessage = baseFormat
                        .replace("%player_name%", player.getName())
                        .replace("%message%", formattedMessage);

                online.sendMessage(MessageUtils.colorize(finalMessage));
            }
        }
    }
}
