package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public abstract class SynchronizedRunnable implements Runnable {
    protected final Plugin plugin;
    protected final BukkitScheduler scheduler;
    
    protected void syncSleep(final long ticks) {
        scheduler.scheduleSyncDelayedTask(plugin, this, ticks);
    }
    
    protected SynchronizedRunnable(final Plugin plugin, final BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }
    
    protected SynchronizedRunnable(final Plugin plugin, final BukkitScheduler scheduler, final long initialSleepTime) {
        this(plugin, scheduler);
        syncSleep(initialSleepTime);
    }
}
