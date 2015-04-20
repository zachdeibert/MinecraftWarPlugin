package com.gitlab.zachdeibert.WarPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DeathHandler implements Listener {
    private final Map<Player, List<ItemStack>> inventories;
    private double dropChance;
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathMessageGeneration(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Location loc = player.getLocation();
        String message = event.getDeathMessage();
        String suffix = "";
        if ( message.endsWith(".") ) {
            message = message.substring(0, message.length() - 1);
            suffix = ".";
        }
        message += String.format(" at (%d, %d, %d)%s", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), suffix);
        event.setDeathMessage(message);
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if ( inventories.containsKey(player) ) {
            final List<ItemStack> items = inventories.get(player);
            inventories.remove(player);
            final PlayerInventory inv = player.getInventory();
            for ( final ItemStack stack : items ) {
                inv.addItem(stack);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final List<ItemStack> drops = event.getDrops();
        event.setKeepLevel(true);
        final List<ItemStack> removedDrops = new ArrayList<ItemStack>();
        final int numberOfDrops = (int) (drops.size() * dropChance);
        drops.sort(new RandomComparator<ItemStack>(dropChance));
        for ( int i = 0; i < numberOfDrops; i++ ) {
            removedDrops.add(drops.get(0));
            drops.remove(0);
        }
        inventories.put(player, removedDrops);
    }
    
    public void setDropChance(final double chance) {
        dropChance = chance;
    }
    
    public void load(final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat(".Drop.Chance"), 0.1);
        setDropChance(config.getDouble(prefix.concat(".Drop.Chance")));
    }
    
    public DeathHandler() {
        inventories = new HashMap<Player, List<ItemStack>>();
        dropChance = 0.1;
    }
}
