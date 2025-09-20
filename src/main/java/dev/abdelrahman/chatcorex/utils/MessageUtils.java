package dev.abdelrahman.chatcorex.utils;

import dev.abdelrahman.chatcorex.Chatcorex;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static String colorize(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Applies anti-swear filtering to a message.
     * Returns the censored message and notifies staff if needed.
     */
    public static String filterSwear(Chatcorex plugin, Player sender, String message) {
        if (!plugin.getConfig().getBoolean("anti-swear.enabled", true)) return message;

        List<String> blocked = plugin.getConfig().getStringList("anti-swear.blocked-words");
        String filtered = message;
        boolean found = false;

        for (String word : blocked) {
            // Escape regex special characters
            String safeWord = word.replaceAll("([\\\\.^$|?*+()\\[\\]{}])", "\\\\$1");

            // Leet / symbol bypass variations
            String regex = safeWord
                    .replace("a", "[a4@]")
                    .replace("i", "[i1!|]")
                    .replace("e", "[e3]")
                    .replace("o", "[o0]")
                    .replace("u", "[uüv]")
                    .replace("s", "[s$5z]")
                    .replace("c", "[c(¢k]")
                    .replace("b", "[b8]");

            // Ensure we only match the word as a standalone word (not part of other words)
            regex = "(?i)(?<![a-zA-Z0-9])" + regex + "(?![a-zA-Z0-9])";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(filtered);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String stars = "*".repeat(matcher.group().length());
                matcher.appendReplacement(sb, stars);
                found = true;
            }
            matcher.appendTail(sb);
            filtered = sb.toString();
        }

        // Notify staff
        if (found && plugin.getConfig().getBoolean("anti-swear.notify-staff", true)) {
            String permission = plugin.getConfig().getString("anti-swear.staff-permission", "chatcorex.antiswear.notify");
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (staff.hasPermission(permission)) {
                    staff.sendMessage(colorize("&c[AntiSwear] &f" + sender.getName() +
                            " tried to say: &7" + message));
                }
            }
        }

        return filtered;
    }
}
