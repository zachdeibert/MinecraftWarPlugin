package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class SpawnEggHandler extends ShapedRecipe {
    private enum MobIds {
        Creeper(50, Material.SULPHUR),
        Skeleton(51, Material.ARROW),
        Spider(52, Material.SPIDER_EYE),
        Zombie(54, Material.ROTTEN_FLESH),
        Slime(55, Material.SLIME_BALL),
        Ghast(56, Material.GHAST_TEAR),
        ZombiePigman(57, Material.GOLD_INGOT),
        Enderman(58, Material.ENDER_PEARL),
        CaveSpider(59, Material.STRING),
        Silverfish(60, Material.COBBLESTONE),
        Blaze(61, Material.BLAZE_ROD),
        MagmaCube(62, Material.MAGMA_CREAM),
        Pig(90, Material.PORK),
        Sheep(91, Material.WOOL),
        Cow(62, Material.RAW_BEEF),
        Chicken(93, Material.RAW_CHICKEN),
        Squid(94, Material.INK_SACK),
        Wolf(95, Material.BONE),
        Mooshroom(96, Material.RED_MUSHROOM),
        SnowGolem(97, Material.SNOW_BLOCK),
        Ocelot(98, Material.RAW_FISH),
        IronGolem(99, Material.IRON_BLOCK),
        Villager(120, Material.EMERALD);
        
        public final short id;
        public final Material mainDrop;
        
        MobIds(final int id, final Material mainDrop) {
            this.id = (short) id;
            this.mainDrop = mainDrop;
        }
    }
    
    public static void setup(final Server server, final FileConfiguration config, final String prefix) {
        for ( final MobIds mob : MobIds.values() ) {
            final String key = prefix.concat(".Enabled.").concat(mob.name());
            config.addDefault(key, true);
            if ( config.getBoolean(key) ) {
                server.addRecipe(new SpawnEggHandler(mob));
            }
        }
        server.addRecipe(new SpawnEggHandler());
    }
    
    private static ItemStack getBlankSpawnEgg(final int amount) {
        final ItemStack stack = new ItemStack(Material.MONSTER_EGG);
        stack.setAmount(amount);
        return stack;
    }
    
    private static ItemStack getSpawnEgg(final MobIds mob) {
        final ItemStack stack = getBlankSpawnEgg(1);
        stack.setDurability(mob.id);
        return stack;
    }
    
    private SpawnEggHandler() {
        super(getBlankSpawnEgg(64));
        shape("eee", "ede", "eee");
        setIngredient('d', Material.DIAMOND);
        setIngredient('e', Material.EGG);
    }
    
    private SpawnEggHandler(final MobIds mob) {
        super(getSpawnEgg(mob));
        shape(" d ", "dbd", " d ");
        setIngredient('b', Material.MONSTER_EGG);
        setIngredient('d', mob.mainDrop);
    }
}
