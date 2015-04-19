package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WarEnder implements Listener {
    private final Server server;
    private final int x;
    private final int y;
    private final int z;
    private final Runnable onWin;
    private int radius;
    public boolean warStarted = false;
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player players[] = server.getOnlinePlayers();
        if ( warStarted && players.length > 1 ) {
            Player winner = null;
            for ( final Player player : players ) {
                final Location loc = player.getLocation();
                final int x = loc.getBlockX() - this.x;
                final int y = loc.getBlockY() - this.y;
                final int z = loc.getBlockZ() - this.z;
                final double radius = Math.sqrt(x * x + y * y + z * z);
                if ( radius >= this.radius ) {
                    if ( winner == null ) {
                        winner = player;
                    } else {
                        return;
                    }
                }
            }
            if ( winner != null ) {
                Util.broadcastMessage("§2The war has ended!§r");
                Util.broadcastMessage(String.format("§2%s has won the war!§r", winner.getName()));
                if ( onWin != null ) {
                    onWin.run();
                }
            }
        }
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public void load(final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat(".Radius"), 32);
        setRadius(config.getInt(prefix.concat(".Radius")));
    }
    
    public WarEnder(final Server server, final Runnable callback) {
        this.server = server;
        final Location spawn = server.getWorlds().get(0).getSpawnLocation();
        x = spawn.getBlockX();
        y = spawn.getBlockY();
        z = spawn.getBlockZ();
        onWin = callback;
    }
    
    public WarEnder(final Server server) {
        this(server, null);
    }
}
