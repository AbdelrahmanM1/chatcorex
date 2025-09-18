package dev.abdelrahman.chatcorex.commands;

import dev.abdelrahman.chatcorex.Chatcorex;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteChatCommand implements CommandExecutor {

    private final Chatcorex plugin;

    public MuteChatCommand(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("chatcorex.mutechat")) {
            sender.sendMessage(ChatColor.RED + "You don’t have permission to mute the chat.");
            return true;
        }

        // Toggle mute
        boolean newState = !plugin.isChatMuted();
        plugin.setChatMuted(newState);

        // Broadcast to all players
        if (newState) {
            sender.getServer().broadcastMessage(ChatColor.RED + "⚠ Global chat has been muted by " + sender.getName() + "!");
        } else {
            sender.getServer().broadcastMessage(ChatColor.GREEN + "✅ Global chat has been unmuted by " + sender.getName() + "!");
        }

        return true;
    }
}
