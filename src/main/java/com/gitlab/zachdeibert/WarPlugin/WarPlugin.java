package com.gitlab.zachdeibert.WarPlugin;

import java.io.File;
import net.minecraft.server.Item;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WarPlugin extends JavaPlugin {
    private static final byte IS_LOADED  = 1;
    private static final byte IS_ENABLED = 2;
    private static final byte WAR_IS_STARTING = 4;
    private static final byte PLAYERS_ARE_FROZEN = 8;
    private byte state;
    private final PlayerFreezer freezer = new PlayerFreezer();
    private WarStarter war;
    private WarEnder ender;
    private StartingInventory inv;
    private SpawnTeleporter tper;
    private Healer healer;
    private SpawnSphere sphere;
    
    @Override
    public boolean isInitialized() {
        return (state & IS_LOADED) != 0;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if ( (state & IS_ENABLED) != 0 ) {
            final String name = command.getName();
            if ( sender instanceof ConsoleCommandSender ) {
                if ( name.equalsIgnoreCase("startWar") ) {
                    if ( (state & WAR_IS_STARTING) == 0 ) {
                        sender.sendMessage("Beginning war setup.");
                        war = new WarStarter(this, getServer().getScheduler(), inv, tper, getConfig().getInt("War.Start.Countdown.Seconds"));
                        freezer.enable();
                        state |= WAR_IS_STARTING | PLAYERS_ARE_FROZEN;
                    } else {
                        sender.sendMessage("The war is already starting.");
                    }
                } else if ( name.equalsIgnoreCase("freezePlayers") ) {
                    if ( (state & PLAYERS_ARE_FROZEN) == 0 ) {
                        sender.sendMessage("Freezing players...");
                        freezer.enable();
                        state |= PLAYERS_ARE_FROZEN;
                    } else {
                        sender.sendMessage("The players are already frozen.");
                    }
                } else if ( name.equalsIgnoreCase("thawPlayers") ) {
                    if ( (state & PLAYERS_ARE_FROZEN) != 0 ) {
                        if ( (state & WAR_IS_STARTING) == 0 ) {
                            sender.sendMessage("Thawing players...");
                            freezer.disable();
                            state ^= PLAYERS_ARE_FROZEN;
                        } else {
                            sender.sendMessage("Starting war.");
                            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                                @Override
                                public void run() {
                                    freezer.disable();
                                    ender.warStarted = true;
                                    state ^= PLAYERS_ARE_FROZEN;
                                }
                            }, getConfig().getInt("War.Start.Countdown.Seconds") * 20);
                            war.start();
                            state ^= WAR_IS_STARTING;
                        }
                    } else {
                        sender.sendMessage("The players are already thawed.");
                    }
                } else if ( name.equalsIgnoreCase("generateSpawnSphere") ) {
                    sphere.generate();
                } else if ( name.equalsIgnoreCase("clearLag") ) {
                    for ( World world : getServer().getWorlds() ) {
                        for ( Entity entity : world.getEntitiesByClasses(Item.class) ) {
                            entity.remove();
                        }
                    }
                } else {
                    return false;
                }
                return true;
            } else if ( sender instanceof Player ) {
                if ( name.equalsIgnoreCase("heal") ) {
                    if ( !healer.healPlayer((Player) sender) ) {
                        sender.sendMessage("§4You have to wait before healing yourself again.§r");
                    }
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        state ^= IS_ENABLED;
    }

    @Override
    public void onEnable() {
        try {
            ItemDeserializer.init();
            final Server server = getServer();
            final World world = server.getWorlds().get(0);
            final PluginManager pluginManager = server.getPluginManager();
            final FileConfiguration config = getConfig();
            final File dataFolder = getDataFolder();
            if ( config.getBoolean("General.Reset", false) ) {
                new File(dataFolder, "config.yml").renameTo(new File(dataFolder, "config.yml.bk"));
                reloadConfig();
            }
            config.options().copyDefaults(true);
            config.addDefault("Healing.Cooldown", 60000);
            config.addDefault("War.Start.Countdown.Seconds", 10);
            config.addDefault("Apocalypse.Enable", false);
            config.addDefault("General.Lock", false);
            config.addDefault("General.Reset", false);
            healer = new Healer(config.getInt("Healing.Cooldown"));
            inv = new StartingInventory(config, "War.Start.Inventory");
            tper = new SpawnTeleporter(world);
            tper.load(config, "War.Start.Teleporting");
            sphere = new SpawnSphere(this);
            sphere.load(config, "War.Spawn.Sphere");
            ender = new WarEnder(server);
            ender.load(config, "War.Winning");
            final StatisticsHandler stats = new StatisticsHandler();
            stats.load(dataFolder);
            SpawnEggHandler.setup(server, config, "Monsters.Eggs");
            ExpHandler.setup(server, config, "Exp");
            if ( !config.getBoolean("General.Lock") ) {
                saveConfig();
            }
            pluginManager.registerEvents(freezer, this);
            pluginManager.registerEvents(ender, this);
            pluginManager.registerEvents(stats, this);
            if ( config.getBoolean("Apocalypse.Enable") ) {
                final ZombieApocalypseMode apocalypse = new ZombieApocalypseMode();
                pluginManager.registerEvents(apocalypse, this);
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
            state |= IS_ENABLED;
        }
    }

    @Override
    public void onLoad() {
        state |= IS_LOADED;
    }
}
