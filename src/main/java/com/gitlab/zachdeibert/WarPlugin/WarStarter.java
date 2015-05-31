package com.gitlab.zachdeibert.WarPlugin;

import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class WarStarter extends SynchronizedRunnable {
    private enum State {
        TPING, COUNTDOWN, STARTING
    }
    
    private final SpawnTeleporter tper;
    private State                 state;
    private int                   count;
    private Consumer<Player>      onStart;
    
    @Override
    public void run() {
        switch ( state ) {
            case TPING:
                for ( final Player player : Bukkit.getOnlinePlayers() ) {
                    tper.teleportPlayer(player);
                }
                syncSleep(40);
                break;
            case COUNTDOWN:
                Util.broadcastMessage(String.format("%d...", count--));
                syncSleep(20);
                if ( count <= 0 ) {
                    state = State.STARTING;
                }
                break;
            case STARTING:
                Util.broadcastMessage("Go!");
                for ( final Player player : Bukkit.getOnlinePlayers() ) {
                    onStart.accept(player);
                }
                break;
        }
    }
    
    public void load(final Consumer<Player> onStart) {
        this.onStart = onStart;
    }
    
    public void start() {
        state = State.COUNTDOWN;
    }
    
    public WarStarter(final Plugin plugin, final BukkitScheduler scheduler, final SpawnTeleporter tper, final int count) {
        super(plugin, scheduler, 40);
        this.count = count;
        this.tper = tper;
        state = State.TPING;
        tper.clearArea();
    }
}
