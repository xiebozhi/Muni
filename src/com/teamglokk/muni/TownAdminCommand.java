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
        } else if (split[0].equalsIgnoreCase("reload") ) {
            player.sendMessage("Reloading config");
            plugin.reloadConfig();
            return true;
        }  else if (split[0].equalsIgnoreCase("listCits") ) {
            player.sendMessage("Here is the list of citizens: ");
            for (Citizen c : plugin.citizens ){
                player.sendMessage(c.getName() );
            }
            return true;
        } else if (split[0].equalsIgnoreCase("save")) {
            player.sendMessage("Saving config - doesn't really do anything!");
            plugin.saveConfig();
            return true;
        } else if (split[0].equalsIgnoreCase("debug")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            if (split[1].equalsIgnoreCase("on") ){
                plugin.setDebug(true);
            } else if (split[1].equalsIgnoreCase("off") ){
                plugin.setDebug(false);
            }
            player.sendMessage("Debug changed to "+split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("addTown")) {
            if (split.length != 3) {
                player.sendMessage("Incorrect number of parameters");
                return false;
            }
            // Check to see of the player is real and connected
            Player temp = null;
            temp = plugin.getServer().getPlayer(split[2]);            
            if (temp != null){
                   plugin.towns.add( new Town (plugin,split[1],split[2] ) ) ;
                   player.sendMessage("Added the town: "+split[2] );
                return true; 
            } else {
                player.sendMessage("Could not add the town, check the logs");
                return false;
            }
            //} else { return false; }
        } else if (split[0].equalsIgnoreCase("addCitizen")) {
            if (split.length != 3) {
                player.sendMessage("Not enough parameters: /townadmin addcitizen townName playerName");
                return false;
            }
            Citizen temp = new Citizen (plugin,split[1],split[2] );
            plugin.addCtizien(temp);
            player.sendMessage("Added " +split[2]+" to "+ split[1] );
            return true;
        }  else if (split[0].equalsIgnoreCase("removeTown")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            plugin.removeTown( split[1] );
            player.sendMessage("Removed town: "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("removeCitizen")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            plugin.removeCitizen( split[1] );
            player.sendMessage("Removed player " + split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("checkBal")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            // Check to see of the player is real and connected
            Player temp = null;
            temp = plugin.getServer().getPlayer(split[1]);            
            if (temp != null){
                if (split[1].equalsIgnoreCase(player.getName() ) ) {
                    player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance(player));
                } else {
                    player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance( temp ));
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
            if (plugin.econwrapper.payItemR( player, plugin.rankupItemID, amount,"Test" ) ){
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
                player.sendMessage("Not enough parameters;");
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
                player.sendMessage(player.getDisplayName()+" is Op" );
            }else {
                player.sendMessage(player.getDisplayName()+" is not Op" );
            }
            return true;
        } else if (split[0].equalsIgnoreCase("tp")) { //Transform to ticketing system in time
            if (split.length != 4) {
                player.sendMessage("Not enough parameters;");
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
                player.sendMessage("Given location is invalid");
                return false;
            }
        }  else if (split[0].equalsIgnoreCase("test")) { //DELETE MEEEEEE
            player.sendMessage("The command was: "+command.toString() );
            return true;
        } else {
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
