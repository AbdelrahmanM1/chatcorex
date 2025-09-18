package dev.abdelrahman.chatcorex.listeners;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.commands.StaffChatCommand;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

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

            String format = plugin.getConfig().getString("staff-chat.format", "&8[Staff] &b%player_name% &7» &f%message%")
                    .replace("%player_name%", player.getName())
                    .replace("%message%", message);

            // Send to online staff members
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission(permission)) {
                    online.sendMessage(MessageUtils.colorize(format));
                }
            }
        }
    }
}
