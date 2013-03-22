/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni.listeners;

import com.teamglokk.muni.Muni;
import java.util.Date;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Bobb
 */
public class MuniHeartbeat extends BukkitRunnable {
    private final Muni plugin;
    
    public MuniHeartbeat (Muni plugin) {
        this.plugin = plugin;
    }
    
    public void run() { 
        
        plugin.getLogger().severe("[Muni Heartbeat] ");
        
        long start = System.currentTimeMillis();
        int roundTo = 30; // 0 <= rT <= 60
        Date roundedHalf = new Date( Math.round ( (double) start / ( roundTo*60*1000 ) ) * (roundTo*60*1000) );
        Date roundedHour = new Date( Math.round ( (double) start / ( roundTo*2*60*1000 ) ) * (roundTo*2*60*1000) );
        Date exactDate = new Date (start);
        
        plugin.getServer().broadcastMessage("Voting time!");
        // check for past due votes
            // announce reminders on the upcoming votes for the next 24 hours
            // decide their fate and announce to the appropriate town
        // begin voting on new ballots
        
        
        if ( false ) { 
            plugin.getServer().broadcastMessage( exactDate.toString() );
            plugin.getServer().broadcastMessage( roundedHalf.toString() );
            plugin.getServer().broadcastMessage( roundedHour.toString() );
        }
    }
}
