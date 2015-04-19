package com.gitlab.zachdeibert.WarPlugin;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class StartingInventory {
    private static final ItemStack DEFAULT_HELMET = new ItemStack(Material.DIAMOND_HELMET);
    private static final ItemStack DEFAULT_CHESTPLATE = new ItemStack(Material.DIAMOND_CHESTPLATE);
    private static final ItemStack DEFAULT_LEGGINGS = new ItemStack(Material.DIAMOND_LEGGINGS);
    private static final ItemStack DEFAULT_BOOTS = new ItemStack(Material.DIAMOND_BOOTS);
    private static final ItemStack DEFAULT_SWORD = new ItemStack(Material.DIAMOND_SWORD);
    private final int health;
    private final int food;
    private final int expLevel;
    private final float exp;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final List<ItemStack> inventory;
    
    public void giveTo(final Player player) {
        player.setHealth(health);
        player.setFoodLevel(food);
        final int startingExp = player.getTotalExperience();
        player.setLevel(expLevel);
        player.setExp(exp);
        player.setTotalExperience(startingExp + player.getTotalExperience());
        final PlayerInventory inv = player.getInventory();
        if ( inv.getHelmet() == null ) {
            inv.setHelmet(helmet);
        } else {
            inv.addItem(helmet);
        }
        if ( inv.getChestplate() == null ) {
            inv.setChestplate(chestplate);
        } else {
            inv.addItem(chestplate);
        }
        if ( inv.getLeggings() == null ) {
            inv.setLeggings(leggings);
        } else {
            inv.addItem(leggings);
        }
        if ( inv.getBoots() == null ) {
            inv.setBoots(boots);
        } else {
            inv.addItem(boots);
        }
        for ( ItemStack item : inventory ) {
            inv.addItem(item);
        }
    }
    
    public StartingInventory(final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat(".Attributes.Health"), 20);
        config.addDefault(prefix.concat(".Attributes.Food"), 20);
        config.addDefault(prefix.concat(".Attributes.XP.Level"), 0);
        config.addDefault(prefix.concat(".Attributes.XP.XP"), 0.0);
        config.addDefault(prefix.concat(".Armor.Helmet"), DEFAULT_HELMET);
        config.addDefault(prefix.concat(".Armor.Chestplate"), DEFAULT_CHESTPLATE);
        config.addDefault(prefix.concat(".Armor.Leggings"), DEFAULT_LEGGINGS);
        config.addDefault(prefix.concat(".Armor.Boots"), DEFAULT_BOOTS);
        health = config.getInt(prefix.concat(".Attributes.Health"));
        food = config.getInt(prefix.concat(".Attributes.Food"));
        expLevel = config.getInt(prefix.concat(".Attributes.XP.Level"));
        exp = (float) config.getDouble(prefix.concat(".Attributes.XP.XP"));
        helmet = config.getItemStack(prefix.concat(".Armor.Helmet"));
        chestplate = config.getItemStack(prefix.concat(".Armor.Chestplate"));
        leggings = config.getItemStack(prefix.concat(".Armor.Leggings"));
        boots = config.getItemStack(prefix.concat(".Armor.Boots"));
        inventory = new LinkedList<ItemStack>();
        boolean addDefaults = true;
        for ( String key : config.getKeys(true) ) {
            if ( key.matches(prefix.replace(".", "\\.").concat("\\.Items\\..*")) ) {
                ItemStack stack = config.getItemStack(key);
                if ( stack != null ) {
                    inventory.add(stack);
                    addDefaults = false;
                }
            }
        }
        if ( addDefaults ) {
            config.set(prefix.concat(".Items.Sword"), DEFAULT_SWORD);
            inventory.add(DEFAULT_SWORD);
        }
    }
}
