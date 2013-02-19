/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni.listeners;

import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.teamglokk.muni.Muni;
/**
 *
 * @author shieldsr
 */
public class MuniLoginEvent implements Listener{
    Muni plugin;
    
    public MuniLoginEvent (Muni instance) {
        plugin = instance; 
    }
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("[Muni] Login Message: "+event.getEventName() );
        // Will get changed to updating last login for citizens
        // if town officer, show applicants
        // if invitee, display Invite
    }
}
