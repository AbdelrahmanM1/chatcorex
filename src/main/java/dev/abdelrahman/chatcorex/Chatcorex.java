package dev.abdelrahman.chatcorex;

import dev.abdelrahman.chatcorex.commands.*;
import dev.abdelrahman.chatcorex.listeners.ChatListener;
import dev.abdelrahman.chatcorex.listeners.StaffChatListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Chatcorex extends JavaPlugin {

    private static Chatcorex instance;
    private boolean chatMuted = false;

    @Override
    public void onEnable() {
        instance = this;

        // Load or create config.yml
        saveDefaultConfig();

        // Register listener
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new StaffChatListener(this), this);

        // Register commands
        getCommand("msg").setExecutor(new MsgCommand());
        getCommand("reply").setExecutor(new ReplyCommand());
        getCommand("mutechat").setExecutor(new MuteChatCommand(this));
        getCommand("sc").setExecutor(new StaffChatCommand(this));
        getCommand("chatreload").setExecutor(new ReloadCommand(this));

        getLogger().info("Chatcorex enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Chatcorex disabled!");
    }

    public static Chatcorex getInstance() {
        return instance;
    }

    public boolean isChatMuted() {
        return chatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        this.chatMuted = chatMuted;
    }
}
