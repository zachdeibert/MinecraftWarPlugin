package com.gitlab.zachdeibert.WarPlugin;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class StatisticsHandler implements Listener {
    private File              databaseFile;
    private YamlConfiguration database;
    private Consumer<Player>  onFirstLogin;
    
    public void load(final File dir, final Consumer<Player> onFirstLogin) throws IOException {
        this.onFirstLogin = onFirstLogin;
        try {
            if ( !dir.exists() ) {
                dir.mkdirs();
            }
            databaseFile = new File(dir, "statistics.yml");
            if ( !databaseFile.exists() ) {
                databaseFile.createNewFile();
            }
            database = new YamlConfiguration();
            database.load(databaseFile);
        } catch ( final InvalidConfigurationException ex ) {
            throw new IOException(ex);
        }
    }
    
    public int getDeaths(final String name) throws IOException {
        if ( database == null ) {
            throw new IOException("Not yet loaded");
        }
        return database.getInt(name, 0);
    }
    
    public int getDeaths(final Player player) throws IOException {
        return getDeaths(player.getName());
    }
    
    public void setDeaths(final String name, final int deaths) throws IOException {
        if ( database == null ) {
            throw new IOException("Not yet loaded");
        }
        database.set(name, deaths);
        database.save(databaseFile);
    }
    
    public void setDeaths(final Player player, final int deaths) throws IOException {
        setDeaths(player.getName(), deaths);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) throws IOException {
        final Player player = event.getEntity();
        final String name = player.getName();
        final int deaths = getDeaths(name) + 1;
        setDeaths(name, deaths);
        event.setDeathMessage(String.format("%s [Death #%d]", event.getDeathMessage(), deaths));
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final String name = player.getName();
        if ( !database.contains(name) && onFirstLogin != null ) {
            onFirstLogin.accept(player);
        }
    }
}
