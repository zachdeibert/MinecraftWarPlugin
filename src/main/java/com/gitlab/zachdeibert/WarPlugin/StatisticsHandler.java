package com.gitlab.zachdeibert.WarPlugin;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class StatisticsHandler implements Listener {
    private final ScoreboardManager scoreboardManger;
    private final Scoreboard        scoreboard;
    private final Objective         deaths;
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) throws IOException {
        final Player player = event.getEntity();
        final String name = player.getName();
        final int deaths = this.deaths.getScore(name).getScore();
        final String deathMessage = String.format("%s [Death #%d]", event.getDeathMessage(), deaths);
        event.setDeathMessage(deathMessage);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerLoginEvent event) throws IOException {
        final Player player = event.getPlayer();
        final String name = player.getName();
        final Score score = deaths.getScore(name);
        if ( score == null || score.getScore() == 0 ) {
            score.setScore(1);
            score.setScore(0);
        }
    }
    
    public StatisticsHandler() throws IOException {
        scoreboardManger = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManger.getMainScoreboard();
        Objective deaths = null;
        Objective kills = null;
        for ( final Objective obj : scoreboard.getObjectives() ) {
            final String name = obj.getName();
            if ( name.equals("Deaths") ) {
                deaths = obj;
                if ( kills != null ) {
                    break;
                }
            } else if ( name.equals("Kills") ) {
                kills = obj;
                if ( deaths != null ) {
                    break;
                }
            }
        }
        if ( deaths == null ) {
            deaths = scoreboard.registerNewObjective("Deaths", "dummy");
        }
        if ( kills == null ) {
            kills = scoreboard.registerNewObjective("Kills", "totalKills");
        }
        deaths.setDisplaySlot(DisplaySlot.SIDEBAR);
        kills.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        this.deaths = deaths;
    }
}
