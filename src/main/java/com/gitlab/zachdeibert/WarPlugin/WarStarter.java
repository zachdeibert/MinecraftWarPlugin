package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class WarStarter extends SynchronizedRunnable {
    private enum State {
        TPING,
        COUNTDOWN,
        STARTING
    }

    private final StartingInventory inv;
    private final SpawnTeleporter tper;
    private State state;
    private int count;
    
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
                    inv.giveTo(player);
                }
                break;
        }
    }
    
    public void start() {
        state = State.COUNTDOWN;
        tper.clearArea();
    }
    
    public WarStarter(final Plugin plugin, final BukkitScheduler scheduler, final StartingInventory inv, final SpawnTeleporter tper, final int count) {
        super(plugin, scheduler, 40);
        this.count = count;
        this.inv = inv;
        this.tper = tper;
        state = State.TPING;
    }
}
