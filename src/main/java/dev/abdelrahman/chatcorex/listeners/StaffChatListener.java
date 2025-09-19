package dev.abdelrahman.chatcorex.listeners;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.commands.StaffChatCommand;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class StaffChatListener implements Listener {

    private final Chatcorex plugin;

    public StaffChatListener(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String permission = plugin.getConfig().getString("staff-chat.permission", "chatcorex.staffchat");

        // Check if the player has staff chat toggled
        if (StaffChatCommand.isStaffChatToggled(player)) {
            event.setCancelled(true); // prevent message from going to global chat

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

                    // Ohighlight for the mentioned player
                    if (message.toLowerCase().contains(online.getName().toLowerCase())) {
                        formattedMessage = message.replaceAll(
                                "(?i)" + online.getName(),
                                ChatColor.YELLOW + online.getName() + ChatColor.RESET
                        );
                        online.playSound(online.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                    }
                    String finalMessage = baseFormat
                            .replace("%player_name%", player.getName())
                            .replace("%message%", formattedMessage);

                    online.sendMessage(MessageUtils.colorize(finalMessage));
                }
            }
        }
    }
}
