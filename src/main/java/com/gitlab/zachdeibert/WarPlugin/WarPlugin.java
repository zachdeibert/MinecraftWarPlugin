package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class WarPlugin extends JavaPlugin {
    private static final byte IS_LOADED  = 1;
    private static final byte IS_ENABLED = 2;
    private byte state;
    
    @Override
    public boolean isInitialized() {
        return (state & IS_LOADED) != 0;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        return false;
    }

    @Override
    public void onDisable() {
        state ^= IS_ENABLED;
    }

    @Override
    public void onEnable() {
        state |= IS_ENABLED;
    }

    @Override
    public void onLoad() {
        state |= IS_LOADED;
    }
}
