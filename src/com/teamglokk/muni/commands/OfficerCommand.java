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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
/**
 * Handler for the /town command.
 * @author BobbShields
 */
public class OfficerCommand implements CommandExecutor {
    private Muni plugin;
    private Player player;
    private boolean console = false;
    
    public OfficerCommand (Muni instance){
            console = true;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot send deputy or mayor commands from the console");
            return true;
        }
        player = (Player) sender;

        if (split.length == 0){
            displayHelp(sender);
            return false;
        } else if (split[0].equalsIgnoreCase("help")  ) {
            displayHelp(sender);
            return true;
        } else if (split[0].equalsIgnoreCase("found") ||split[0].equalsIgnoreCase("charter") ||split[0].equalsIgnoreCase("add")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            if (plugin.econwrapper.pay(player, plugin.townRanks[1].getMoneyCost(),
                    plugin.townRanks[1].getItemCost(), "Founding a town" ) ){
                Town t = new Town( plugin, split[1], player.getName() );
                plugin.towns.put(t.getName(), t );
                plugin.allCitizens.put(player.getName(), t.getName() );
                
                player.sendMessage("You have founded "+t.getName());
            } else { player.sendMessage("Could not found the town" ); }
            return true;
        } else if (split[0].equalsIgnoreCase("invite")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.invite(plugin.getServer().getPlayer(split[1]),player);
            player.sendMessage("Invitation to "+temp.getName()+" was sent.");
            return true;
            
        }  else if (split[0].equalsIgnoreCase("decline")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.declineApplication(plugin.getServer().getPlayer(split[1] ) );
            return true;
            
        }  else if (split[0].equalsIgnoreCase("accept")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            player.sendMessage( player.getName() );
            player.sendMessage( plugin.allCitizens.get( "bobbshields" ) );
            player.sendMessage( plugin.getTownName( player.getName() ) );
            String townN = plugin.getTownName( player.getName() );
            Town temp = plugin.getTown( townN );
            temp.acceptApplication(plugin.getServer().getPlayer(split[1]), player);
            return true;
            
        } /*else if (split[0].equalsIgnoreCase("delete") ||split[0].equalsIgnoreCase("disband")) {
            // This does not but should remove all players from citizens who are members of town
            Town temp = plugin.getTown( plugin.getTownName(player) );
            plugin.removeTown(temp.getName() );
            player.sendMessage("Removed: "+ temp.getName() );
            return true;
        }*/ else if (split[0].equalsIgnoreCase("checkTaxes")) {
            player.sendMessage("Checking taxes in time.  Use the DB for now");
            return true;
        }  else if (split[0].equalsIgnoreCase("setTax")) {
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.setTaxRate(Double.parseDouble(split[1]) );
            player.sendMessage("You have set the tax rate for "+temp.getName()+ " to "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("kick")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.removeCitizen(plugin.getServer().getPlayer( split[1] ), player );
            return true;
            
        } else if (split[0].equalsIgnoreCase("bank")) {
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            player.sendMessage("The town bank has "+ temp.getBankBal()+" "+
                    plugin.econwrapper.getCurrName(temp.getBankBal()) );
            // withdrawl
            //deposit
            
            return true;
        } else if (split[0].equalsIgnoreCase("deputize")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.makeDeputy(plugin.getServer().getPlayer(split[1] ),player);
            return true;
            
        } else if (split[0].equalsIgnoreCase("resign")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            if ( temp.isMayor(player) ) {
                temp.resignMayor(player);
                return true;
            } else if ( temp.isDeputy(player) ) {
                temp.resignDeputy(player);
                return true;
            }
        } else if (split[0].equalsIgnoreCase("rankup")) {
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.rankup(player);
            return true;
            
        } else if (split[0].equalsIgnoreCase("setTax")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.setTaxRate(Double.parseDouble(split[1]) ) ;
            return true;
        } else {
            displayHelp(player);
            return true;
        }
        return false;
    }
    private void displayHelp(CommandSender player){ 
            plugin.out(player, "Deputy command help.  You can do these commands:");
            plugin.out(player, "/deputy invite");
            plugin.out(player, "/deputy accept");
            plugin.out(player, "/deputy decline");
            plugin.out(player, "/deputy kick");
            //plugin.out(player, "/deputy regions");
            //plugin.out(player, "/deputy setRegion");
            //plugin.out(player, "/deputy checkTaxes");
            //plugin.out(player, "**/deputy pushBorder");
            plugin.out(player, "**/deputy setTax");
            plugin.out(player, "**These commands can be run as deputy if the permissions are there");
            //plugin.out(player, "Mayor command help.  You can do all the above commands with /mayor and these:");
            plugin.out(player, "/mayor bank");
            //plugin.out(player, "/mayor bank deposit");
            //plugin.out(player, "/mayor bank withdraw");
            plugin.out(player, "/mayor deputize");
            plugin.out(player, "/mayor resign");
            plugin.out(player, "/mayor delete");
            plugin.out(player, "/mayor rankup");
    }
        
   
}
