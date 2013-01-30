package com.teamglokk.muni;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.teamglokk.muni.Muni;
import com.teamglokk.muni.WGWrapper;
import com.teamglokk.muni.EconWrapper;
import com.teamglokk.muni.dbWrapper;
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
        } 
        player = (Player) sender;

        if (split.length == 0){
            displayHelp(player);
            return false;
        } else if (split[0].equalsIgnoreCase("help")  ) {
            displayHelp(player);
            return true;
        } else if (split[0].equalsIgnoreCase("start")  ) {
            if (plugin.dbwrapper.createDB(false) ){
                player.sendMessage("You created the database");
                return true;
            } else {
                player.sendMessage("");
                return false;   }
        } else if (split[0].equalsIgnoreCase("reload") ) {
            player.sendMessage("Reloading config");
            plugin.reloadConfig();
            return true;
        } else if (split[0].equalsIgnoreCase("save")) {
            player.sendMessage("Saving config");
            plugin.saveConfig();
            return true;
        } else if (split[0].equalsIgnoreCase("addTown")) {
            player.sendMessage("Admin adding of towns is not yet enabled");
            if (split.length != 3) {
                player.sendMessage("incorrect number of parameters");
                return false;
            }
            player.sendMessage(split[1]+", "+split[2]);
            //plugin.towns.add(new Town (plugin) );
            //if (plugin.towns.iterator().next().addTown(
            Player temp = null;
            temp = plugin.getServer().getPlayer(split[2]);
            if (temp != null){
                    plugin.towns.addTown( temp, split[1]);//{ //need to verify this player is online
                return true; // or override the addTown method for using a string
            } else {return false;}
            //} else { return false; }
        } else if (split[0].equalsIgnoreCase("addCitizen")) {
            player.sendMessage("Admin adding of citizens is not yet enabled");
            return true;
        }  else if (split[0].equalsIgnoreCase("removeTown")) {
            player.sendMessage("Admin removal of towns is not yet enabled");
            return true;
        } else if (split[0].equalsIgnoreCase("removeCitizen")) {
            player.sendMessage("Admin removing of citizens is not yet enabled");
            return true;
        } else if (split[0].equalsIgnoreCase("checkBal")) {
            
            player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance(player));
            
            return true;
        } else if (split[0].equalsIgnoreCase("pay")) {
            double amount = Double.parseDouble(split[1]) ;
            if (plugin.econwrapper.pay(player, amount)){
                player.sendMessage("You paid " + amount  );
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
            if (player.hasPermission(split[1]) ){
                player.sendMessage(player.getDisplayName()+" has permission: "+ split[1] );
            }else {
                player.sendMessage(player.getDisplayName()+" does not have permission: "+ split[1] );
            }
            return true;
        }else if (split[0].equalsIgnoreCase("testop")) {
            if (player.isOp() ){
                player.sendMessage(player.getDisplayName()+" is Op" );
            }else {
                player.sendMessage(player.getDisplayName()+" is not Op" );
            }
            return true;
        } else if (split[0].equalsIgnoreCase("tp")) { //Transform to ticketing system in time
            player.sendMessage("TP to pos.");
            try {
                double x = Double.parseDouble( split[1] );
                double y = Double.parseDouble( split[2] );
                double z = Double.parseDouble( split[3] );

                player.teleport(new Location(player.getWorld(), x, y, z));
                return true;
            } catch (NumberFormatException ex) {
                player.sendMessage("Given location is invalid");
                return false;
            }
        }  else {
            displayHelp(player);
            return true;
        }
    }
    private void displayHelp(Player player){
            player.sendMessage("TownAdmin Help.  You can do these commands:");
            player.sendMessage("/townAdmin addTown");
            player.sendMessage("/townAdmin removeTown");
            player.sendMessage("/townAdmin setTax");
            player.sendMessage("/townAdmin addCitizen");
            player.sendMessage("/townAdmin removeCitizen");
            player.sendMessage("/townAdmin deputize");
            player.sendMessage("/townAdmin list");
            player.sendMessage("/townAdmin save");
            player.sendMessage("/townAdmin reload");
            player.sendMessage("/townAdmin checkBal");   
    }
   
}
