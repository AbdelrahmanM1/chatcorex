package dev.abdelrahman.chatcorex.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static String setPlaceholders(Player player, String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            String result = PlaceholderAPI.setPlaceholders(player, text);

            // Debug logging to see what's happening
            if (!result.equals(text)) {
                Bukkit.getLogger().info("[ChatCoreX] PlaceholderAPI processed: '" + text + "' -> '" + result + "'");
            } else if (text.contains("%rankcorex_")) {
                Bukkit.getLogger().warning("[ChatCoreX] RankCorex placeholders not processed: " + text);
            }

            return result;
        } else {
            Bukkit.getLogger().warning("[ChatCoreX] PlaceholderAPI not found! Placeholders will not work.");
            return text;
        }
    }
}