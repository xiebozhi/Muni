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
package com.teamglokk.muni.commands;

import com.teamglokk.muni.Citizen;
import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.bukkit.ChatColor;

import java.util.Iterator;
/**
 * Handler for the /town command.
 * @author BobbShields
 */
public class TownCommand implements CommandExecutor {
    private Muni plugin;
    private Player player;
    private boolean console = false;
    
    public TownCommand (Muni instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            console = true;
        } else { player = (Player) sender; }
        
        if (split.length == 0){
            displayHelp(sender);
            return false;
        } else if (split[0].equalsIgnoreCase("help") ) {
            displayHelp(sender);
            return true;
        } else if (split[0].equalsIgnoreCase("payTaxes")) {
            
            Town temp = plugin.getTown( plugin.getTownName(player) );
            if (split.length == 2 ) {
                Double amount = Double.parseDouble(split[1]);
                //player.sendMessage("Paying taxes: "+amount+" to "+temp.getName()+"." );
                return temp.payTaxes(player, amount );
                 
            } else if ( split.length == 1 ){
                return temp.payTaxes(player);
                
            } else { return false; }
        } else if (split[0].equalsIgnoreCase("list")) {
            if (split.length != 1) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            player.sendMessage("List of towns:");
            // iteration will be required here
            Iterator<Town> itr = plugin.towns.iterator();
            if (!itr.hasNext() ){
                plugin.getLogger().info("/town list: No towns to check");
                return false;
            }
            while (itr.hasNext() ){
                Town current = itr.next();
                player.sendMessage(current.getName() ) ;
            }
            return true;
        } else if (split[0].equalsIgnoreCase("info")) {
            if(split.length!=2){
                player.sendMessage("Not the right number of parameters"); 
                return false;
            }
            player.sendMessage( ChatColor.BLUE+ "Info on: " + split[1] );
            //player.sendMessage( plugin.getTown( split[1] ).info() );
            plugin.getTown( split[1] ).info(player);
                    
            return true;
        } else if (split[0].equalsIgnoreCase("apply")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            if (!plugin.isCitizen(player.getName()) ){
                Citizen temp = new Citizen( plugin ) ;
                temp.apply4Citizenship(split[1], player.getName() );
                plugin.citizens.add( temp );
                player.sendMessage("Application to "+temp.getTown()+" was sent.");
                return true;
            } else { 
                player.sendMessage("You are already engaged with "+plugin.allCitizens.get(player.getName() ) );
                player.sendMessage("To clear your status, do /town leave");
                return true;
            }
        } else if (split[0].equalsIgnoreCase("accept")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.acceptInvite(player);
            
            player.sendMessage("Accepted an invite from " + temp.getName() );
            return true;
        } else if (split[0].equalsIgnoreCase("leave")) {
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.leave(player);
            return true;
        }else if (split[0].equalsIgnoreCase("sethome")) {
            player.sendMessage("Sethome not yet added.");
            return true;
        }else if (split[0].equalsIgnoreCase("vote")) {
            player.sendMessage("Voting not yet added.");
            return true;
        } else if (split[0].equalsIgnoreCase("checkBank")) {
            Town temp = plugin.getTown( plugin.getTownName(player) );
            player.sendMessage(temp.getName()+" has bank balance of "+temp.getBankBal());
            return true;
        }  else if (split[0].equalsIgnoreCase("signCharter")) {
            player.sendMessage("Charters not yet enabled ");
            return true;
        } else {
            displayHelp(player);
            return false;
        }
    }
    private void displayHelp(CommandSender player){
        plugin.out( player,ChatColor.LIGHT_PURPLE+"Muni Help.  You can do these commands:");
        plugin.out( player, "/town list");
        plugin.out( player, "/town info");
        plugin.out( player, "/town apply");
        plugin.out( player, "/town accept");
        plugin.out( player, "/town leave");
        //player.sendMessage("/town sethome");
        //player.sendMessage("/town signCharter");
        //player.sendMessage("/town vote");
        plugin.out( player, "/town payTaxes");
        plugin.out( player, "/town checkBank");
    }
   
}
