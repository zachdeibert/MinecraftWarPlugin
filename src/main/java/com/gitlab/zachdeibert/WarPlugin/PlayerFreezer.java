package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerFreezer implements Listener {
    protected boolean enabled = false;
    
    public void enable() {
        enabled = true;
    }
    
    public void disable() {
        enabled = false;
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if ( enabled ) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if ( enabled ) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            from.setPitch(to.getPitch());
            from.setYaw(to.getYaw());
            event.setTo(from);
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if ( enabled && event.getDamager() instanceof Player ) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityInteract(final EntityInteractEvent event) {
        if ( enabled ) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if ( enabled ) {
            event.setCancelled(true);
        }
    }
}
