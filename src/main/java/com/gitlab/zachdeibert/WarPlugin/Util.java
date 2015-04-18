package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {
    public static final double τ = Math.PI * 2;
    
    public static void broadcastMessage(final String msg) {
        for ( final Player player : Bukkit.getOnlinePlayers() ) {
            player.sendMessage(msg);
        }
        Bukkit.getConsoleSender().sendMessage(msg);
    }
}
