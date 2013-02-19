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
    
    public TownAdminCommand (Muni instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        String [] args = trimSplit(split);
        
        if (!(sender instanceof Player)) {
        } else { player = (Player) sender; }

        if (args.length == 0){
            displayHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("help")  ) { // working - 18 Feb
            displayHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("makeTest")  ) { //delete meeeeeee 
            plugin.out(sender,"Dropping the database!  Then re-creating");
            plugin.dbwrapper.createDB(true);
            plugin.makeDefaultCitizens();
            plugin.makeTestTowns();
            return true;
        } else if (args[0].equalsIgnoreCase("reload") ) { // Config reload tested good, towns reload added - 18 Feb
            plugin.out(sender, "Reloading config & towns");
            plugin.getLogger().info(sender.getName()+" issued the reload command");
            plugin.reloadConfig();
            plugin.getLogger().info("Config reloaded");
            plugin.towns.clear();
            plugin.getLogger().info("Towns cleared");
            plugin.loadTowns();
            plugin.getLogger().info("Towns reloaded");
            plugin.out(sender, "Finished");
            return true;
        } else if (args[0].equalsIgnoreCase("save")) { //tested and working - 18 Feb
            plugin.out(sender, "Saving Towns and Citizens to the database!");
            plugin.saveTowns();
            return true;
        } else if (args[0].equalsIgnoreCase("debug")) {
            if (args.length != 2) {
                plugin.out(sender, "/townadmin debug on|off");
            }
            if (args[1].equalsIgnoreCase("on") ){
                plugin.setDebug(true);
            } else if (args[1].equalsIgnoreCase("off") ){
                plugin.setDebug(false);
            } else {
                plugin.out(sender, "/townadmin debug on|off");
            }
            plugin.out(sender, "Debug changed to "+args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("SQLdebug")) { //added but not tested - 18 Feb
            if (args.length != 2) {
                plugin.out(sender, "/townadmin SQLdebug on|off");
            }
            if (args[1].equalsIgnoreCase("on") ){
                plugin.setSQLDebug(true);
            } else if (args[1].equalsIgnoreCase("off") ){
                plugin.setSQLDebug(false);
            } else {
                plugin.out(sender, "/townadmin SQLdebug on|off");
            }
            plugin.out(sender, "SQL_Debug changed to "+args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("addTown")) { //working but changed to save to db, not fully tested - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "Incorrect number of parameters: /townadmin addTown townName mayorName");
                return false;
            }
                Town t = new Town (plugin,args[1],args[2]);
                t.saveToDB();
                plugin.towns.put( t.getName(), t ) ;
                plugin.out(sender, "Added the town: "+args[1] );
                return true; 
                
        } else if (args[0].equalsIgnoreCase("addCitizen")) { //was kinda working, now maybe fixed - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "/townAdmin addCitizen <townName> <playerName>");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( args[1] ) );
            temp.admin_makeCitizen(sender, args[2] ) ;
            plugin.out(sender,args[2]+" was added to "+args[1]); 
            return true;
            
        }  else if (args[0].equalsIgnoreCase("removeTown")) { // The NPE error might be fixed - 18 Feb
            if (args.length != 2) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            plugin.removeTown( args[1] );
            plugin.out(sender, "Removed town: "+ args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("removeCitizen")) { // changed, not tested - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "/townAdmin removeCitizens <townName> <playerName>");
                return true;
            }
            Town temp = plugin.getTown( plugin.getTownName( args[1] ) );
            temp.admin_removeCitizen( player, args[2] );
            return true;
        } else if (args[0].equalsIgnoreCase("check")) { // been working since early on - 18 Feb
            player.sendMessage("Checking build perms.");
            if (plugin.wgwrapper.checkBuildPerms(player) ){
               player.sendMessage("You may build here");
            } else{
                player.sendMessage("You may not build here.");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("tp")) { //Transform to ticketing system in time
            if (args.length != 4) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            player.sendMessage("TP to pos.");
            try {
                double x = Double.parseDouble( args[1] );
                double y = Double.parseDouble( args[2] );
                double z = Double.parseDouble( args[3] );

                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException ex) {
                plugin.out(sender, "Given location is invalid");
                return false;
            }
        }  else if (args[0].equalsIgnoreCase("listCits")) { //DELETE MEEEEEE
            for (Town t : plugin.towns.values() ){
                plugin.out(sender, "Displaying players for "+t.getName() );
                t.listAllCitizens(player);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("listallCits")) { //DELETE MEEEEEE
            for (String c : plugin.allCitizens.keySet() ){
                plugin.out(sender,  c+" "+plugin.allCitizens.get(c) );
            }
            return true;
        } else if (args[0].equalsIgnoreCase("test")) { //DELETE MEEEEEE
            
            player.sendMessage("Here!");
            player.sendMessage(plugin.getTownName("bobbshields") );
            return true;
        } else {
            displayHelp(sender);
            return true;
        }
    }
    private void displayHelp(CommandSender sender){
            plugin.out(sender, "TownAdmin Help.  You can do these commands:");
            plugin.out(sender, "/townAdmin addTown <townName> <mayorName>");
            plugin.out(sender, "/townAdmin removeTown <townName>");
            plugin.out(sender, "/townAdmin setTax <townName> <taxRate>");
            plugin.out(sender, "/townAdmin addCitizen <townName> <playerName>");
            plugin.out(sender, "/townAdmin removeCitizen <townName> <playerName>");
            plugin.out(sender, "/townAdmin deputize <playerName> ");
            plugin.out(sender, "/townAdmin save");
            plugin.out(sender, "/townAdmin reload");  
            plugin.out(sender, "/townAdmin debug <off/on>");   
            plugin.out(sender, "/townAdmin SQLdebug <off/on>");   
    }
       
    private String [] trimSplit (String [] split ) {
        if (split.length > 7) {
            plugin.getLogger().warning("trimSplit: more than 7 parameters so skipping"); 
            return null; 
        }
        String [] rtn = new String[7];
        int i = 0;
        for (String entry: split) {
            if (entry.equalsIgnoreCase(" ") ){
                // do nothing (delete the empty space entries)
            } else {
                rtn[i++] = entry.trim();
            }
        }
        String temp[] = rtn;
        rtn = new String[i];
        int j = 0;
        for (String s: temp){
            rtn[j++] = s;
        }
        return rtn;
    }
}
