package dev.abdelrahman.chatcorex.commands;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Chatcorex plugin;

    public ReloadCommand(Chatcorex plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("chatcorex.reload")) {
            sender.sendMessage(MessageUtils.colorize("&cYou don’t have permission to use this command!"));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(MessageUtils.colorize("&a✔ Chatcorex config reloaded!"));
        return true;
    }
}
