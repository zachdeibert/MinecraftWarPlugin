package com.gitlab.zachdeibert.WarPlugin;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Healer {
    protected final Map<String, Long> playerTimeouts;
    protected final long healTimeout;
    
    protected void doHeal(final Player player) {
        for ( final PotionEffectType effect : PotionEffectType.values() ) {
            if ( effect != null ) {
                player.removePotionEffect(effect);
            }
        }
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        playerTimeouts.put(player.getName(), System.currentTimeMillis());
    }
    
    public boolean canHealPlayer(final Player player) {
        final Long lastUse = playerTimeouts.get(player.getName());
        return lastUse == null || lastUse + healTimeout <= System.currentTimeMillis();
    }
    
    public boolean healPlayer(final Player player) {
        if ( canHealPlayer(player) ) {
            doHeal(player);
            return true;
        } else {
            return false;
        }
    }
    
    public Healer(final long healTimeout) {
        this.healTimeout = healTimeout;
        this.playerTimeouts = new HashMap<String, Long>();
    }
}
