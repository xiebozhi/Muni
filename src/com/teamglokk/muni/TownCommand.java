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
import com.teamglokk.muni.WGWrapper;
import com.teamglokk.muni.EconWrapper;
import com.teamglokk.muni.dbWrapper;

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
        }
        player = (Player) sender;
        
        if (split.length == 0){
            displayHelp(player);
            return false;
        } else if (split[0].equalsIgnoreCase("help") ) {
            displayHelp(player);
            return true;
        } else if (split[0].equalsIgnoreCase("payTaxes")) {
            
            Town temp = plugin.getTown(player);
            if (split.length == 2 ) {
                Double amount = Double.parseDouble(split[1]);
                player.sendMessage("Paying taxes: "+amount+" to "+temp.getName()+"." );
                return temp.tb_deposit(player, amount );
                 
            } else if ( split.length == 1 ){
                return temp.tb_deposit(player, temp.getTaxRate() );
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
            player.sendMessage( ChatColor.DARK_BLUE+ "Info on: " + split[1] );
            Iterator<Town> itr = plugin.towns.iterator();
            while (itr.hasNext() ){
                Town current = itr.next();
                if (current.getName().equals(split[1] ) ){
                    player.sendMessage(current.toDB_Vals() ) ;
                    return true;
                } else { return false; }
            }
            return false;
        } else if (split[0].equalsIgnoreCase("apply")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen temp = new Citizen( plugin ) ;
            temp.apply4Citizenship(split[1], player.getName() );
            plugin.citizens.add( temp );
            player.sendMessage("Application to "+temp.getTown()+" was sent.");
            return true;
        } else if (split[0].equalsIgnoreCase("accept")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen temp = plugin.getCitizen( plugin.getServer().getPlayer(split[1]).getName() );
            if ( temp.isInvited() ){
                temp.makeMember();
            }
            
            player.sendMessage("Accept an invite from " + temp.getTown() );
            return true;
        } else if (split[0].equalsIgnoreCase("leave")) {
            player.sendMessage("Leaving town not yet possible.");
            return true;
        }else if (split[0].equalsIgnoreCase("sethome")) {
            player.sendMessage("Sethome not yet added.");
            return true;
        }else if (split[0].equalsIgnoreCase("vote")) {
            player.sendMessage("Voting not yet added.");
            return true;
        } else if (split[0].equalsIgnoreCase("checkBank")) {
            Town temp = new Town( plugin.getTown(player) );
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
    private void displayHelp(Player player){
        player.sendMessage(ChatColor.DARK_PURPLE+"Muni Help.  You can do these commands:");
            player.sendMessage("/town list");
            player.sendMessage("/town info");
            player.sendMessage("/town join");
            player.sendMessage("/town add");
            player.sendMessage("/town remove");
            player.sendMessage("/town sethome");
            player.sendMessage("/town vote");
            player.sendMessage("/town payTaxes");
    }
   
}
