package dev.abdelrahman.chatcorex.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static String setPlaceholders(Player player, String text) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
