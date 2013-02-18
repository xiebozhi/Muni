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
import org.bukkit.Location;
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
public class TownAdminCommand implements CommandExecutor {
    private Muni plugin;
    private Player player;
    private boolean console = false;
    
    public TownAdminCommand (Muni instance){
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
        } else if (split[0].equalsIgnoreCase("help")  ) {
            displayHelp(sender);
            return true;
        } else if (split[0].equalsIgnoreCase("makeTest")  ) {
            plugin.out(sender,"Dropping the database!  Then re-creating");
            plugin.dbwrapper.createDB(true);
            plugin.makeDefaultCitizens();
            plugin.makeDefaultTowns();
            return true;
        } else if (split[0].equalsIgnoreCase("reload") ) {
            plugin.out(sender, "Reloading config");
            plugin.reloadConfig();
            plugin.getLogger().info("Config reloaded");
            return true;
        } else if (split[0].equalsIgnoreCase("save")) {
            plugin.out(sender, "Saving Towns and Citizens to the database!");
            plugin.saveTowns();
            return true;
        } else if (split[0].equalsIgnoreCase("debug")) {
            if (split.length != 2) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            if (split[1].equalsIgnoreCase("on") ){
                plugin.setDebug(true);
            } else if (split[1].equalsIgnoreCase("off") ){
                plugin.setDebug(false);
            }
            plugin.out(sender, "Debug changed to "+split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("addTown")) {
            if (split.length != 3) {
                plugin.out(sender, "Incorrect number of parameters: /townadmin addTown townName mayorName");
                return false;
            }
                Town t = new Town (plugin,split[1],split[2] );
                plugin.towns.put( t.getName(), t ) ;
                plugin.out(sender, "Added the town: "+split[1] );
                return true; 
                
        } else if (split[0].equalsIgnoreCase("addCitizen")) {
            if (split.length != 3) {
                plugin.out(sender, "Incorrect number of parameters: /townadmin addcitizen townName playerName");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.admin_makeCitizen(split[1] ) ;
            return true;
            
        }  else if (split[0].equalsIgnoreCase("removeTown")) {
            if (split.length != 2) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            plugin.removeTown( split[1] );
            plugin.out(sender, "Removed town: "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("removeCitizen")) {
            if (split.length != 2) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( player.getName() ) );
            temp.admin_removeCitizen( split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("checkBal")) {
            if (split.length != 2) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            // Check to see of the player is real and connected
            Player temp = null;
            temp = plugin.getServer().getPlayer(split[1]);            
            if (temp != null){
                if (split[1].equalsIgnoreCase(player.getName() ) ) {
                    player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance(player));
                } else {
                    plugin.out(sender, "Your balance is "+ plugin.econwrapper.getBalance( temp ));
                }
            } else {
                player.sendMessage("Could not check the offline player's balance");
            }
            player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance(player));
            
            return true;
        } else if (split[0].equalsIgnoreCase("pay")) {
            double amount = Double.parseDouble(split[1]) ;
            if (plugin.econwrapper.payMoneyR( player, amount,"Test" ) ){
                return true;
            } else {return false;}
            
        } else if (split[0].equalsIgnoreCase("payTest")) {
            if (plugin.econwrapper.pay( player, 10.0, 16,"Test" ) ){
                return true;
            } else {return false;}
            
        } else if (split[0].equalsIgnoreCase("payItem")) {
            int amount = Integer.parseInt( split[1] ) ;
            if (plugin.econwrapper.payItemR( player, plugin.getRankupItemID(), amount,"Test" ) ){
                return true;
            } else {return false;}
            
        } else if (split[0].equalsIgnoreCase("check")) {
            player.sendMessage("Checking build perms.");
            if (plugin.wgwrapper.checkBuildPerms(player) ){
               player.sendMessage("You may build here");
            } else{
                player.sendMessage("You may not build here.");
            }
            return true;
        } else if (split[0].equalsIgnoreCase("testperms")) {
            if (split.length != 2) {
                player.sendMessage("Incorrect number of parameters;");
                return false;
            }
            if (player.hasPermission(split[1]) ){
                player.sendMessage(player.getDisplayName()+" has permission: "+ split[1] );
            }else {
                player.sendMessage(player.getDisplayName()+" does not have permission: "+ split[1] );
            }
            return true;
        }else if (split[0].equalsIgnoreCase("testop")) {
            if (player.isOp() ){
                plugin.out(player, player.getDisplayName()+" is Op" );
            }else {
                plugin.out(player, player.getDisplayName()+" is not Op" );
            }
            return true;
        } else if (split[0].equalsIgnoreCase("tp")) { //Transform to ticketing system in time
            if (split.length != 4) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            player.sendMessage("TP to pos.");
            try {
                double x = Double.parseDouble( split[1] );
                double y = Double.parseDouble( split[2] );
                double z = Double.parseDouble( split[3] );

                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException ex) {
                plugin.out(sender, "Given location is invalid");
                return false;
            }
        }  else if (split[0].equalsIgnoreCase("test")) { //DELETE MEEEEEE
            plugin.out(sender, "The command was: "+command.toString() );
            return true;
        } else if (split[0].equalsIgnoreCase("testEnum")) { //DELETE MEEEEEE
            Citizen c = new Citizen (plugin);
            plugin.out(sender, "The enum result is "+c.getRoleFromEnum(split[1] ) );
            return true;
        } else if (split[0].equalsIgnoreCase("listCits")) { //DELETE MEEEEEE
            for (Town t : plugin.towns.values() ){
                plugin.out(sender, "Displaying players for "+t.getName() );
                t.listAllCitizens(player);
            }
            return true;
        } else if (split[0].equalsIgnoreCase("listallCits")) { //DELETE MEEEEEE
            for (String c : plugin.allCitizens.keySet() ){
                plugin.out(sender,  c+" "+plugin.allCitizens.get(c) );
            }
            return true;
        } else {
            displayHelp(sender);
            return true;
        }
    }
    private void displayHelp(CommandSender player){
            plugin.out(player, "TownAdmin Help.  You can do these commands:");
            plugin.out(player, "/townAdmin addTown");
            plugin.out(player, "/townAdmin removeTown");
            plugin.out(player, "/townAdmin setTax");
            plugin.out(player, "/townAdmin addCitizen");
            plugin.out(player, "/townAdmin removeCitizen");
            plugin.out(player, "/townAdmin deputize");
            plugin.out(player, "/townAdmin list");
            plugin.out(player, "/townAdmin save");
            plugin.out(player, "/townAdmin reload");
            plugin.out(player, "/townAdmin checkBal");   
            plugin.out(player, "/townAdmin debug (off/on)");   
    }
   
}
