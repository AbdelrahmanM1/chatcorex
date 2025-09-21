package dev.abdelrahman.chatcorex.commands;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /reply <message>");
            return true;
        }

        // Check if player has someone to reply to
        Player target = MsgCommand.lastMessaged.get(player);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "You have no one to reply to!");
            return true;
        }

        // Combine message
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String message = sb.toString().trim();

        // Apply anti-swear filter
        String filteredMessage = MessageUtils.filterSwear(Chatcorex.getInstance(), player, message);

        // Send filtered messages
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[To " + target.getName() + "] " + ChatColor.WHITE + filteredMessage);
        target.sendMessage(ChatColor.LIGHT_PURPLE + "[From " + player.getName() + "] " + ChatColor.WHITE + filteredMessage);

        // Update last messaged
        MsgCommand.lastMessaged.put(player, target);
        MsgCommand.lastMessaged.put(target, player);

        return true;
    }
}
