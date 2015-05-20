package com.gitlab.zachdeibert.WarPlugin;

import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class SpawnSphere extends SynchronizedRunnable {
    private static final ItemStack DEFAULT_TYPE = new ItemStack(Material.STONE);
    private Location sphereBlocks[];
    private World world;
    private int x;
    private int y;
    private int z;
    private int radius;
    private int typeId;
    private int timeout;
    private boolean displayMessage;
    private boolean running;
    
    public void generate() {
        if ( displayMessage ) {
            Util.broadcastMessage("§bRegenerating Spawn Sphere§r");
        }
        for ( final Location loc : sphereBlocks ) {
            final Block block = world.getBlockAt(loc);
            block.setTypeId(typeId);
        }
    }
    
    private void regenerateBlocksArray() {
        if ( world != null ) {
            final List<Location> blocks = new LinkedList<Location>();
            for ( int x = 0; x < radius; x++ ) {
                for ( int y = 0; y < radius; y++ ) {
                    for ( int z = 0; z < radius; z++ ) {
                        final double radius = Math.sqrt(x * x + y * y + z * z);
                        if ( radius < this.radius && radius > this.radius - 1 ) {
                            final int px = x + this.x;
                            final int py = y + this.y;
                            final int pz = z + this.z;
                            final int nx = -x + this.x;
                            final int ny = -y + this.y;
                            final int nz = -z + this.z;
                            blocks.add(new Location(world, px, py, pz));
                            blocks.add(new Location(world, px, py, nz));
                            blocks.add(new Location(world, px, ny, pz));
                            blocks.add(new Location(world, px, ny, nz));
                            blocks.add(new Location(world, nx, py, pz));
                            blocks.add(new Location(world, nx, py, nz));
                            blocks.add(new Location(world, nx, ny, pz));
                            blocks.add(new Location(world, nx, ny, nz));
                        }
                    }
                }
            }
            blocks.add(new Location(world, x + radius - 1, y, z));
            blocks.add(new Location(world, x - radius + 1, y, z));
            blocks.add(new Location(world, x, y + radius - 1, z));
            blocks.add(new Location(world, x, y - radius + 1, z));
            blocks.add(new Location(world, x, y, z + radius - 1));
            blocks.add(new Location(world, x, y, z - radius + 1));
            blocks.add(new Location(world, x, y - 1, z));
            final List<Location> filteredBlocks = new LinkedList<Location>();
            for ( final Location loc : blocks ) {
                if ( !filteredBlocks.contains(loc) ) {
                    filteredBlocks.add(loc);
                }
            }
            sphereBlocks = filteredBlocks.toArray(new Location[0]);
        }
    }
    
    public void setCenter(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        regenerateBlocksArray();
    }
    
    public void setCenter(final Location loc) {
        setCenter(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public void setCenter(final World world) {
        this.world = world;
        setCenter(world.getSpawnLocation());
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
        regenerateBlocksArray();
        
    }
    
    public void setType(final int typeId) {
        this.typeId = typeId;
    }
    
    public void setType(final Block type) {
        setType(type.getTypeId());
    }
    
    public void setType(final ItemStack type) {
        setType(type.getTypeId());
    }
    
    public void setTimeout(final int ticks) {
        timeout = ticks;
    }
    
    public void setDisplayMessage(final boolean display) {
        displayMessage = display;
    }
    
    public void load(final FileConfiguration config, final String prefix) {
        final Server server = plugin.getServer();
        config.addDefault(prefix.concat(".World"), 0);
        config.addDefault(prefix.concat(".Radius"), 16);
        config.addDefault(prefix.concat(".Block"), DEFAULT_TYPE);
        config.addDefault(prefix.concat(".Regeneration.Time"), 1200);
        config.addDefault(prefix.concat(".Regeneration.Automatic"), true);
        config.addDefault(prefix.concat(".Regeneration.Message"), true);
        setRadius(config.getInt(prefix.concat(".Radius")));
        setType(config.getItemStack(prefix.concat(".Block")));
        setTimeout(config.getInt(prefix.concat(".Regeneration.Time")));
        setCycling(config.getBoolean(prefix.concat(".Regeneration.Automatic")));
        setDisplayMessage(config.getBoolean(prefix.concat(".Regeneration.Message")));
        final Object world = config.get(prefix.concat(".World"));
        if ( world instanceof Integer ) {
            setCenter(server.getWorlds().get((int) world));
        } else if ( world instanceof String ) {
            setCenter(server.getWorld((String) world));
        } else {
            config.set(prefix.concat(".World"), 0);
            setCenter(server.getWorlds().get(0));
        }
    }
    
    public void enableCycling() {
        if ( !running ) {
            syncSleep(timeout);
            running = true;
        }
    }
    
    public void disableCycling() {
        running = false;
    }
    
    public void setCycling(final boolean enabled) {
        if ( enabled ) {
            enableCycling();
        } else {
            disableCycling();
        }
    }
    
    @Override
    public void run() {
        generate();
        if ( timeout > 0 ) {
            syncSleep(timeout);
        }
    }
    
    public SpawnSphere(final JavaPlugin plugin, final BukkitScheduler scheduler) {
        super(plugin, scheduler);
    }
    
    public SpawnSphere(final JavaPlugin plugin) {
        this(plugin, plugin.getServer().getScheduler());
    }
}
