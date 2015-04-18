package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ZombieApocalypseMode implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        if ( event.getDamager() instanceof Player && event.getEntity() instanceof Zombie ) {
            final Zombie parent = (Zombie) event.getEntity();
            if ( parent.getHealth() > event.getDamage() ) {
                final Location loc = parent.getLocation();
                final World world = loc.getWorld();
                final Zombie child = (Zombie) world.spawnEntity(loc, EntityType.ZOMBIE);
                child.setHealth(parent.getHealth());
                child.setTarget(parent.getTarget());
                child.setVelocity(parent.getVelocity());
            }
        }
    }
}
