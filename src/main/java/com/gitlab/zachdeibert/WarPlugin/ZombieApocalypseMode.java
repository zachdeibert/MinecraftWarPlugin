package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;

public class ZombieApocalypseMode implements Listener {
    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        if ( (damager instanceof Player || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player)) && event.getEntity() instanceof Zombie ) {
            final Zombie parent = (Zombie) event.getEntity();
            if ( parent.getHealth() > event.getDamage() ) {
                final Location loc = parent.getLocation();
                final World world = loc.getWorld();
                final Zombie child = (Zombie) world.spawnEntity(loc, EntityType.ZOMBIE);
                for ( final PotionEffect potion : parent.getActivePotionEffects() ) {
                    child.addPotionEffect(potion);
                }
                child.setHealth(parent.getHealth());
                child.setTarget(parent.getTarget());
                child.setVelocity(parent.getVelocity());
            }
        }
    }
}
