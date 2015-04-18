package com.gitlab.zachdeibert.WarPlugin;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnTeleporter {
    private final Random random;
    private final World world;
    private final Location spawn;
    private final int x;
    private final int y;
    private final int z;
    private double radius;
    private double width;
    private int height;
    
    public void teleportPlayer(final Player p) {
        final double θ = Util.τ * random.nextDouble();
        final double x = radius * Math.cos(θ) + this.x;
        final double z = radius * Math.sin(θ) + this.z;
        p.teleport(new Location(world, x, y, z));
    }
    
    public void clearArea() {
        final double hWidth = width / 2;
        final double nhWidth = -hWidth;
        for ( double θ = 0.0; θ < Util.τ; θ += 0.01 ) {
            final double sin = Math.sin(θ);
            final double cos = Math.cos(θ);
            for ( double b = nhWidth; b < hWidth; b++ ) {
                final double radiusPB = radius + b;
                final int x = (int) (cos * radiusPB) + this.x;
                final int z = (int) (sin * radiusPB) + this.z;
                for ( int i = 0; i < height; i++ ) {
                    final Block block = world.getBlockAt(x, y + i, z);
                    block.setType(Material.AIR);
                }
                final Block block = world.getBlockAt(x, y - 1, z);
                if ( block.getType() == Material.AIR ) {
                    block.setType(Material.STONE);
                }
            }
        }
    }
    
    public void setRadius(final double radius) {
        this.radius = radius;
    }
    
    public void setWidth(final double width) {
        this.width = width;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public void load(final FileConfiguration config, final String prefix) {
        config.addDefault(prefix.concat(".Radius"), 20.0);
        config.addDefault(prefix.concat(".Width"), 4);
        config.addDefault(prefix.concat(".Height"), 3);
        setRadius(config.getDouble(prefix.concat(".Radius")));
        setWidth(config.getDouble(prefix.concat(".Width")));
        setHeight(config.getInt(prefix.concat(".Height")));
    }
    
    public SpawnTeleporter(final World world) {
        random = new Random();
        this.world = world;
        spawn = world.getSpawnLocation();
        x = spawn.getBlockX();
        y = spawn.getBlockY();
        z = spawn.getBlockZ();
    }
}
