package dev.abdelrahman.chatcorex.commands;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class StaffChatCommand implements CommandExecutor {

    private final Chatcorex plugin;
    // Tracks which players have staff chat toggled on
    private static final HashMap<UUID, Boolean> toggledStaffChat = new HashMap<>();

    public StaffChatCommand(Chatcorex plugin) {
        this.plugin = plugin;
    }

    public static boolean isStaffChatToggled(Player player) {
        return toggledStaffChat.getOrDefault(player.getUniqueId(), false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        String permission = plugin.getConfig().getString("staff-chat.permission", "chatcorex.staffchat");
        if (!player.hasPermission(permission)) {
            player.sendMessage(MessageUtils.colorize("&cYou do not have permission to use staff chat."));
            return true;
        }

        if (args.length == 0) {
            // Toggle staff chat mode
            boolean enabled = toggledStaffChat.getOrDefault(player.getUniqueId(), false);
            toggledStaffChat.put(player.getUniqueId(), !enabled);
            player.sendMessage(MessageUtils.colorize("&aStaff chat " + (!enabled ? "enabled" : "disabled") + "."));
            return true;
        }

        // If args exist, send message instantly
        String message = String.join(" ", args);
        message = MessageUtils.filterSwear(plugin, player, message);

        String format = plugin.getConfig().getString("staff-chat.format", "&8[Staff] &b%player_name% &7» &f%message%")
                .replace("%player_name%", player.getName())
                .replace("%message%", message);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission(permission)) {
                online.sendMessage(MessageUtils.colorize(format));
            }
        }

        return true;
    }
}
