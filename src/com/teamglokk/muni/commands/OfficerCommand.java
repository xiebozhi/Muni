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
            return false;
        }
        player = (Player) sender;

        if (split.length == 0){
            displayHelp(player);
            return false;
        } else if (split[0].equalsIgnoreCase("help")  ) {
            displayHelp(player);
            return true;
        } else if (split[0].equalsIgnoreCase("found") ||split[0].equalsIgnoreCase("charter") ||split[0].equalsIgnoreCase("add")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            if (plugin.econwrapper.pay(player, plugin.townRanks[0].getMoneyCost(),
                    plugin.townRanks[0].getItemCost(), "Founding a town" ) ){
                Town t = new Town( plugin, split[1], player.getName() );
                plugin.towns.add(t);
                player.sendMessage("You have founded "+t.getName());
            } else { player.sendMessage("Could not found the town" ); }
            return true;
        } else if (split[0].equalsIgnoreCase("invite")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.invite(plugin.getServer().getPlayer(split[1]),player);
            player.sendMessage("Invitation to "+temp.getName()+" was sent.");
            return true;
            
        }  else if (split[0].equalsIgnoreCase("decline")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.declineApplication(plugin.getServer().getPlayer(split[1] ) );
            return true;
            
        }  else if (split[0].equalsIgnoreCase("accept")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
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
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.setTaxRate(Double.parseDouble(split[1]) );
            player.sendMessage("You have set the tax rate for "+temp.getName()+ " to "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("kick")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.removeCitizen(plugin.getServer().getPlayer( split[1] ), player );
            return true;
            
        } else if (split[0].equalsIgnoreCase("bank")) {
            Town temp = plugin.getTown( plugin.getTownName(player) );
            player.sendMessage("The town bank has "+ temp.getBankBal()+" "+
                    plugin.econwrapper.getCurrName(temp.getBankBal()) );
            // withdrawl
            //deposit
            
            return true;
        } else if (split[0].equalsIgnoreCase("deputize")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.makeDeputy(plugin.getServer().getPlayer(split[1] ),player);
            return true;
            
        } else if (split[0].equalsIgnoreCase("resign")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            if ( temp.isMayor(player) ) {
                temp.resignMayor(player);
                return true;
            } else if ( temp.isDeputy(player) ) {
                temp.resignDeputy(player);
                return true;
            }
        } else if (split[0].equalsIgnoreCase("rankup")) {
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.rankup(player);
            return true;
            
        } else if (split[0].equalsIgnoreCase("setTax")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName(player) );
            temp.setTaxRate(Double.parseDouble(split[1]) ) ;
            return true;
        } else {
            displayHelp(player);
            return true;
        }
        return false;
    }
    private void displayHelp(Player player){ 
            player.sendMessage("Deputy command help.  You can do these commands:");
            player.sendMessage("/deputy invite");
            player.sendMessage("/deputy accept");
            player.sendMessage("/deputy decline");
            player.sendMessage("/deputy kick");
            //player.sendMessage("/deputy regions");
            //player.sendMessage("/deputy setRegion");
            //player.sendMessage("/deputy checkTaxes");
            //player.sendMessage("**/deputy pushBorder");
            player.sendMessage("**/deputy setTax");
            player.sendMessage("**These commands can be run as deputy if the permissions are there");
            //player.sendMessage("Mayor command help.  You can do all the above commands with /mayor and these:");
            player.sendMessage("/mayor bank");
            //player.sendMessage("/mayor bank deposit");
            //player.sendMessage("/mayor bank withdraw");
            player.sendMessage("/mayor deputize");
            player.sendMessage("/mayor resign");
            player.sendMessage("/mayor delete");
            player.sendMessage("/mayor rankup");
    }
        
   
}
