package com.gitlab.zachdeibert.WarPlugin;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class ExpHandler extends ShapedRecipe {
    public static void setup(final Server server, final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat("..Bottles.Enabled"), true);
        if ( config.getBoolean(prefix.concat(".Bottles.Enabled")) ) {
            server.addRecipe(new ExpHandler());
        }
    }
    
    private ExpHandler() {
        super(new ItemStack(Material.EXP_BOTTLE));
        shape("sps", "gdg", "dgd");
        setIngredient('d', Material.DIAMOND);
        setIngredient('g', Material.GLASS);
        setIngredient('p', Material.SULPHUR);
        setIngredient('s', Material.GLOWSTONE_DUST);
    }
}
