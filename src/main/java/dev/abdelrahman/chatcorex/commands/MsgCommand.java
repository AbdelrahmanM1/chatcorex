package dev.abdelrahman.chatcorex.commands;

import dev.abdelrahman.chatcorex.Chatcorex;
import dev.abdelrahman.chatcorex.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MsgCommand implements CommandExecutor {

    // Store last messaged players (for /reply command later)
    public static Map<Player, Player> lastMessaged = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(ChatColor.RED + "That player is not online!");
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "You cannot message yourself!");
            return true;
        }

        // Combine message
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String message = sb.toString().trim();

        // Apply anti-swear filter
        String filteredMessage = MessageUtils.filterSwear(Chatcorex.getInstance(), player, message);

        // Send filtered message
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[To " + target.getName() + "] " + ChatColor.WHITE + filteredMessage);
        target.sendMessage(ChatColor.LIGHT_PURPLE + "[From " + player.getName() + "] " + ChatColor.WHITE + filteredMessage);

        // Store last messaged
        lastMessaged.put(player, target);
        lastMessaged.put(target, player);

        return true;
    }
}
