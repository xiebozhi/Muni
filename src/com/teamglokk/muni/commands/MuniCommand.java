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

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
import org.bukkit.ChatColor;
/**
 * Handler for the /town command.
 * @author BobbShields
 */
public class MuniCommand implements CommandExecutor {
    private Muni plugin;
    private Player staffer;
    
    public MuniCommand (Muni instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        String [] args = plugin.trimSplit(split);
        
        if (!(sender instanceof Player)) {
        } else { 
            staffer = (Player) sender; 
            if (!plugin.econwrapper.hasPerm(staffer, "muni.admin") ){
                staffer.sendMessage("You do not have permission to run /muni subcommands");
                return true; 
            }
        }

        if (args.length == 0){
            displayHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("help")  ) { // working - 18 Feb
            displayHelp(sender);
            return true;
        } else if (args[0].equalsIgnoreCase("reload") ) { // Config reload tested good, towns reload added - 18 Feb
            plugin.out(sender, "Reloading config & towns");
            plugin.getLogger().info(sender.getName()+" issued the reload command");
            plugin.reloadConfig();
            plugin.getLogger().info("Config reloaded");
            plugin.towns.clear();
            plugin.getLogger().info("Towns cleared");
            plugin.towns.clear();
            plugin.allCitizens.clear();
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
                plugin.out(sender, "/muni debug on|off");
            }
            if (args[1].equalsIgnoreCase("on") ){
                plugin.setDebug(true);
            } else if (args[1].equalsIgnoreCase("off") ){
                plugin.setDebug(false);
            } else {
                plugin.out(sender, "/muni debug on|off");
            }
            plugin.out(sender, "Debug changed to "+args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("SQLdebug")) { //added but not tested - 18 Feb
            if (args.length != 2) {
                plugin.out(sender, "/muni SQLdebug on|off");
            }
            if (args[1].equalsIgnoreCase("on") ){
                plugin.setSQLDebug(true);
            } else if (args[1].equalsIgnoreCase("off") ){
                plugin.setSQLDebug(false);
            } else {
                plugin.out(sender, "/muni SQLdebug on|off");
            }
            plugin.out(sender, "SQL_Debug changed to "+args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("addTown")) { //working but changed to save to db, not fully tested - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "Incorrect number of parameters: /muni addTown townName mayorName");
                return false;
            }
                Town t = new Town (plugin,args[1],args[2],null);
                t.saveToDB();
                plugin.towns.put( t.getName(), t ) ;
                plugin.out(sender, "Added the town: "+args[1] );
                return true; 
                
        } else if (args[0].equalsIgnoreCase("addCitizen")) { //was kinda working, now maybe fixed - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "/muni addCitizen <townName> <playerName>");
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
            Town temp = plugin.getTown(args[1] ) ;
            temp.removeAllTownCits();
            plugin.removeTown( args[1] );
            plugin.out(sender, "Removed town: "+ args[1] );
            return true;
        } else if (args[0].equalsIgnoreCase("removeCitizen")) { // changed, not tested - 18 Feb
            if (args.length != 3) {
                plugin.out(sender, "/muni removeCitizens <townName> <playerName>");
                return true;
            }
            Town temp = plugin.getTown( plugin.getTownName( args[1] ) );
            temp.admin_removeCitizenship( staffer, args[2] ); //NPE --------------20 feb
            return true;
        } else if (args[0].equalsIgnoreCase("tp")) { //Transform to ticketing system in time
            if (args.length != 4) {
                plugin.out(sender, "Incorrect number of parameters;");
                return false;
            }
            staffer.sendMessage("TP to pos.");
            try {
                double x = plugin.parseD( args[1] );
                double y = plugin.parseD( args[2] );
                double z = plugin.parseD( args[3] );

                staffer.teleport(new Location(staffer.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException ex) {
                plugin.out(sender, "Given location is invalid");
                return false;
            }
        } else if (args[0].equalsIgnoreCase("blankUpdate")  ) { //delete meeeeeee 
            plugin.out(sender,"Dropping the database!  Then re-creating");
            plugin.towns.clear();
            plugin.allCitizens.clear();
            plugin.dbwrapper.createDB(true);
            return true;
        } else if (args[0].equalsIgnoreCase("makeTest")  ) { //delete meeeeeee 
            plugin.out(sender,"Dropping the database!  Then re-creating");
            plugin.towns.clear();
            plugin.allCitizens.clear();
            plugin.dbwrapper.createDB(true);
            plugin.makeDefaultCitizens();
            plugin.makeTestTowns();
            plugin.loadTowns();
            return true;
        } else if (args[0].equalsIgnoreCase("listCits")) { //DELETE MEEEEEE
            for (Town t : plugin.towns.values() ){
                plugin.out(sender, "Displaying players for "+t.getName() );
                t.listAllCitizens(staffer);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("listallCits")) { //DELETE MEEEEEE
            for (String c : plugin.allCitizens.keySet() ){
                plugin.out(sender,  c+" "+plugin.allCitizens.get(c) );
            }
            return true;
        } else if (args[0].equalsIgnoreCase("test")) { //DELETE MEEEEEE
            
            staffer.sendMessage("Here!");
            staffer.sendMessage(plugin.getTownName("bobbshields") );
            return true;
        } else {
            staffer.sendMessage("[Muni] Input not understood.");
            displayHelp(sender);
            return true;
        }
    }
    private void displayHelp(CommandSender sender){
            plugin.out(sender, "Muni Admin Help.  You can do these commands:", ChatColor.LIGHT_PURPLE );
            plugin.out(sender, "/muni addTown <townName> <mayorName>");
            plugin.out(sender, "/muni removeTown <townName>");
            plugin.out(sender, "/muni setTax <townName> <taxRate>");
            plugin.out(sender, "/muni addCitizen <townName> <playerName>");
            plugin.out(sender, "/muni removeCitizen <townName> <playerName>");
            plugin.out(sender, "/muni deputize <playerName> ");
            plugin.out(sender, "/muni save");
            plugin.out(sender, "/muni reload");  
            plugin.out(sender, "/muni debug <off/on>");   
            plugin.out(sender, "/muni SQLdebug <off/on>");   
            plugin.out(sender, "/muni makeTest (Temp! makes a test database)");   
    }
}
