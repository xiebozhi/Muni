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

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
import org.bukkit.ChatColor;
import org.bukkit.event.EventPriority;

/**
 * The custom Muni Login Event
 * @author bobbshields
 */
public class MuniLoginEvent implements Listener{
    Muni plugin;
    
    public MuniLoginEvent (Muni instance) {
        plugin = instance; 
    }
    /**
     * Displays relevant town info to players as they log in. 
     * @param event 
     */
    @EventHandler (priority = EventPriority.LOW)
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (player.isOp() && plugin.EXPECTED_CONFIG_VERSION !=  plugin.CONFIG_VERSION){
            player.sendMessage( ChatColor.RED+"[Muni] The config version is not the latest.");
            player.sendMessage( ChatColor.RED+"[Muni] Please backup the config and delete on the next restart!!!");
            player.sendMessage( ChatColor.RED+"[Muni] When you come back up, you can change options and do /muni reload");
        }
        if (plugin.isCitizen( player ) ){
            Town t = plugin.getTownFromCitizen(player.getName() );
            if (t.isMayor(player) ){
                player.sendMessage(ChatColor.YELLOW+"[Muni] You are the mayor of "+t.getName() );
                if (t.hasApplicants() ){
                    player.sendMessage(ChatColor.YELLOW+"These players are applicants: "+ t.getAllApplicants() );
                }
            } else if (t.isDeputy(player) ) {
                player.sendMessage(ChatColor.YELLOW+"[Muni] You are a deputy of "+t.getName() );
                if (t.hasApplicants() ){
                    player.sendMessage(ChatColor.YELLOW+"These players are applicants: "+ t.getAllApplicants() );
                }
            }else if (t.isCitizen(player) ) {
                player.sendMessage(ChatColor.YELLOW+"[Muni] You are a citizen of "+t.getName() );
            }else if (t.isApplicant(player) ) {
                player.sendMessage(ChatColor.YELLOW+"[Muni] Your application to "+t.getName()+" is pending" );
            }else if (t.isInvited(player) ) {
                player.sendMessage(ChatColor.YELLOW+"[Muni] You are invited to "+t.getName() );
            }
        } else {
            player.sendMessage(ChatColor.YELLOW+"[Muni] You are not a member of a town");
        }
    }
}
