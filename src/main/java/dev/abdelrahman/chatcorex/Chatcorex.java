package dev.abdelrahman.chatcorex;

import dev.abdelrahman.chatcorex.commands.*;
import dev.abdelrahman.chatcorex.listeners.ChatListener;
import dev.abdelrahman.chatcorex.listeners.StaffChatListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Chatcorex extends JavaPlugin {

    private static Chatcorex instance;
    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        instance = this;

        // Display ASCII logo
        displayLogo();

        // Initialize plugin
        long startTime = System.currentTimeMillis();

        try {
            // Load or create config.yml
            log(Level.INFO, "Loading configuration...");
            saveDefaultConfig();
            log(Level.INFO, "Configuration loaded successfully!");

            // Register listeners
            log(Level.INFO, "Registering event listeners...");
            getServer().getPluginManager().registerEvents(new ChatListener(this), this);
            getServer().getPluginManager().registerEvents(new StaffChatListener(this), this);
            log(Level.INFO, "Event listeners registered successfully!");

            // Register commands
            log(Level.INFO, "Registering commands...");
            registerCommands();
            log(Level.INFO, "Commands registered successfully!");

            long loadTime = System.currentTimeMillis() - startTime;
            log(Level.INFO, "Plugin enabled successfully in " + loadTime + "ms!");
            log(Level.INFO, "Author: 3bdoabk | Version: " + getDescription().getVersion());

        } catch (Exception e) {
            log(Level.SEVERE, "Failed to enable plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        log(Level.INFO, "Shutting down ChatCoreX...");

        // Clean up resources if needed
        instance = null;

        log(Level.INFO, "ChatCoreX has been disabled successfully!");
        log(Level.INFO, "Thank you for using ChatCoreX!");
    }

    private void displayLogo() {
        String[] logo = {
                "",
                " §6╔═══════════════════════════════════════════════════════════╗",
                " §6║                                                           ║",
                " §6║  §e  ██████╗██╗  ██╗ █████╗ ████████╗ ██████╗ ██████╗ ██████╗ ███████╗  §6║",
                " §6║  §e ██╔════╝██║  ██║██╔══██╗╚══██╔══╝██╔════╝██╔═══██╗██╔══██╗██╔════╝  §6║",
                " §6║  §e ██║     ███████║███████║   ██║   ██║     ██║   ██║██████╔╝█████╗    §6║",
                " §6║  §e ██║     ██╔══██║██╔══██║   ██║   ██║     ██║   ██║██╔══██╗██╔══╝    §6║",
                " §6║  §e ╚██████╗██║  ██║██║  ██║   ██║   ╚██████╗╚██████╔╝██║  ██║███████╗  §6║",
                " §6║  §e  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝  §6║",
                " §6║                                                           ║",
                " §6║                    §b⚡ Advanced Chat System ⚡                   §6║",
                " §6║                                                           ║",
                " §6╚═══════════════════════════════════════════════════════════╝",
                ""
        };

        for (String line : logo) {
            getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('§', line));
        }
    }

    private void registerCommands() {
        try {
            if (getCommand("msg") != null) {
                getCommand("msg").setExecutor(new MsgCommand());
                log(Level.INFO, "✓ Command 'msg' registered");
            } else {
                log(Level.WARNING, "✗ Command 'msg' not found in plugin.yml");
            }

            if (getCommand("reply") != null) {
                getCommand("reply").setExecutor(new ReplyCommand());
                log(Level.INFO, "✓ Command 'reply' registered");
            } else {
                log(Level.WARNING, "✗ Command 'reply' not found in plugin.yml");
            }

            if (getCommand("mutechat") != null) {
                getCommand("mutechat").setExecutor(new MuteChatCommand(this));
                log(Level.INFO, "✓ Command 'mutechat' registered");
            } else {
                log(Level.WARNING, "✗ Command 'mutechat' not found in plugin.yml");
            }

            if (getCommand("sc") != null) {
                getCommand("sc").setExecutor(new StaffChatCommand(this));
                log(Level.INFO, "✓ Command 'sc' registered");
            } else {
                log(Level.WARNING, "✗ Command 'sc' not found in plugin.yml");
            }

            if (getCommand("chatreload") != null) {
                getCommand("chatreload").setExecutor(new ReloadCommand(this));
                log(Level.INFO, "✓ Command 'chatreload' registered");
            } else {
                log(Level.WARNING, "✗ Command 'chatreload' not found in plugin.yml");
            }

        } catch (Exception e) {
            log(Level.SEVERE, "Error registering commands: " + e.getMessage());
            throw e;
        }
    }

    /**
     * logging method with consistent formatting
     */
    public void log(Level level, String message) {
        String prefix = "[ChatCoreX] ";

        switch (level.toString()) {
            case "INFO":
                prefix += "§a[INFO]§f ";
                break;
            case "WARNING":
                prefix += "§e[WARN]§f ";
                break;
            case "SEVERE":
                prefix += "§c[ERROR]§f ";
                break;
            case "FINE":
                prefix += "§b[DEBUG]§f ";
                break;
            default:
                prefix += "[" + level + "] ";
                break;
        }

        // Send colored message to console
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('§', prefix + message));

        // Also log to standard logger without colors for file logging
        getLogger().log(level, ChatColor.stripColor(message));
    }

    /**
     * Convenience method for info logging
     */
    public void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Convenience method for warning logging
     */
    public void warn(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Convenience method for error logging
     */
    public void error(String message) {
        log(Level.SEVERE, message);
    }

    public static Chatcorex getInstance() {
        return instance;
    }

    public boolean isChatMuted() {
        return chatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        this.chatMuted = chatMuted;
        if (chatMuted) {
            info("Chat has been muted globally");
        } else {
            info("Chat has been unmuted globally");
        }
    }
}