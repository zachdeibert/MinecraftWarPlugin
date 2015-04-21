package com.gitlab.zachdeibert.WarPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DeathHandler implements Listener {
    private final Map<String, List<ItemStack>> inventories;
    private double dropChance;
    
    private void serializeMap() {
        try {
            final File worldDir = Bukkit.getWorlds().get(0).getWorldFolder();
            final File serialFile = new File(worldDir, getClass().getName().concat(".Inventories"));
            final FileConfiguration serial = new YamlConfiguration();
            for ( final String name : inventories.keySet() ) {
                serial.set(name, inventories.get(name));
            }
            serial.save(serialFile);
        } catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }
    
    private Map<String, List<ItemStack>> deserializeMap() {
        try {
            final File worldDir = Bukkit.getWorlds().get(0).getWorldFolder();
            final File serialFile = new File(worldDir, getClass().getName().concat(".Inventories"));
            if ( serialFile.exists() ) {
                final FileConfiguration serial = new YamlConfiguration();
                serial.load(serialFile);
                final Map<String, Object> deserialized = serial.getValues(false);
                final Map<String, List<ItemStack>> map = new HashMap<String, List<ItemStack>>();
                for ( final String name : deserialized.keySet() ) {
                    @SuppressWarnings("unchecked")
                    final List<ItemStack> list = (List<ItemStack>) deserialized.get(name);
                    map.put(name, list);
                }
                return map;
            }
        } catch ( final Exception ex ) {
            ex.printStackTrace();
        }
        return new HashMap<String, List<ItemStack>>();
    }
    
    private List<ItemStack> getList(final String name) {
        if ( inventories.containsKey(name) ) {
            final List<ItemStack> list = inventories.get(name);
            if ( list != null ) {
                return list;
            }
        }
        final List<ItemStack> list = new ArrayList<ItemStack>();
        inventories.put(name, list);
        return list;
    }
    
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
        final PlayerInventory inv = player.getInventory();
        final List<ItemStack> items = getList(player.getName());
        while ( items.size() > 0 ) {
            inv.addItem(items.get(0));
            items.remove(0);
        }
        serializeMap();
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final List<ItemStack> drops = event.getDrops();
        event.setKeepLevel(true);
        final List<ItemStack> removedDrops = getList(player.getName());
        final int numberOfDrops = (int) (drops.size() * dropChance);
        drops.sort(new RandomComparator<ItemStack>(dropChance));
        for ( int i = 0; i < numberOfDrops; i++ ) {
            removedDrops.add(drops.get(0));
            drops.remove(0);
        }
        serializeMap();
    }
    
    public void setDropChance(final double chance) {
        dropChance = 1.0 - chance;
    }
    
    public void load(final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat(".Drop.Chance"), 0.1);
        setDropChance(config.getDouble(prefix.concat(".Drop.Chance")));
    }
    
    public DeathHandler() throws IOException {
        inventories = deserializeMap();
        dropChance = 0.1;
    }
}
