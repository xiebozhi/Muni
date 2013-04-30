/* 
 * Muni 
 * Copyright (C) 2013 bobbshields <https://github.com/xiebozhi/Muni> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Binary releases are available freely at <http://dev.bukkit.org/server-mods/muni/>.
*/
package com.teamglokk.muni.listeners;

import com.teamglokk.muni.Muni;
import java.util.Date;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This heartbeat is a BukkitRunnable task that is scheduled every half hour.
 * It checks voting data and otherwise, and will perform actions as needed.  
 * @author Bobb
 */
public class MuniHeartbeat extends BukkitRunnable {
    private final Muni plugin;
    
    public MuniHeartbeat (Muni plugin) {
        this.plugin = plugin;
    }
    
    public void run() { 
        
        if (plugin.isDebug()) {plugin.getLogger().info("[Muni Heartbeat] "); }
        
        long start = System.currentTimeMillis();
        int roundTo = 30; // 0 <= rT <= 60
        Date roundedHalf = new Date( Math.round ( (double) start / ( roundTo*60*1000 ) ) * (roundTo*60*1000) );
        Date roundedHour = new Date( Math.round ( (double) start / ( roundTo*2*60*1000 ) ) * (roundTo*2*60*1000) );
        Date exactDate = new Date (start);
        
        if (plugin.isDebug()) {plugin.getServer().broadcastMessage("[Muni] Voting coming soon!"); }
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
