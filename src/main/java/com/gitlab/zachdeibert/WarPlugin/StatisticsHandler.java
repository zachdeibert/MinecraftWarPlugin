package com.gitlab.zachdeibert.WarPlugin;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatisticsHandler implements Listener {
    private File databaseFile;
    private YamlConfiguration database;
    
    public void load(File dir) throws IOException {
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
        } catch ( InvalidConfigurationException ex ) {
            throw new IOException(ex);
        }
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) throws IOException {
        final Player player = event.getEntity();
        final String name = player.getName();
        final int deaths = database.getInt(name, 0) + 1;
        database.set(name, deaths);
        database.save(databaseFile);
        final String deathMessage = String.format("%s (Death #%d)", event.getDeathMessage(), deaths);
        event.setDeathMessage(deathMessage);
    }
}
