/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni.listeners;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;

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
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.isCitizen( player ) ){
            Town t = plugin.getTownFromCitizen(player.getName() );
            if (t.isMayor(player) ){
                player.sendMessage("[Muni] You are the mayor of "+t.getName() );
                if (t.hasApplicants() ){
                    player.sendMessage("These players are applicants: "+ t.getAllApplicants() );
                }
            } else if (t.isDeputy(player) ) {
                player.sendMessage("[Muni] You are a deputy of "+t.getName() );
                if (t.hasApplicants() ){
                    player.sendMessage("These players are applicants: "+ t.getAllApplicants() );
                }
            }else if (t.isCitizen(player) ) {
                player.sendMessage("[Muni] You are a citizen of "+t.getName() );
            }else if (t.isApplicant(player) ) {
                player.sendMessage("[Muni] Your application to "+t.getName()+" is pending" );
            }else if (t.isInvited(player) ) {
                player.sendMessage("[Muni] You are invited to "+t.getName() );
            }
        } else {
            player.sendMessage("[Muni] You are not a member of a town");
        }
    }
}
