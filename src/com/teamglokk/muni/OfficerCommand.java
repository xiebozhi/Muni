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
            Citizen tempCit = plugin.getCitizen(player.getName() ) ;
            if (tempCit.isOfficer(plugin.getTown(player).getName() ) ){
                if (!plugin.isCitizen(split[1]) ){
                    tempCit.inviteCitizen(tempCit.getTown(), split[1], player.getName() ); 
                    plugin.citizens.add( tempCit );
                } else {
                    player.sendMessage("Player is already engaged with another town");
                    player.sendMessage("Tell them to do /town leave");
                    return true;
                }
            }
            player.sendMessage("Invitation to "+tempCit.getTown()+" was sent.");
            return true;
            
            
        }  else if (split[0].equalsIgnoreCase("decline")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Citizen tempCit = plugin.getCitizen( split[1] ) ;
            if (tempCit == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            if (officer.isOfficer() ){
                if (officer.getTown().equals(tempCit.getTown() ) ) {
                    plugin.citizens.remove( tempCit );
                    player.sendMessage("You have declined the player");
                } else {player.sendMessage("Player was not a member of your town"); } 
            } else { player.sendMessage("You are not an officer");}
            player.sendMessage("Invitation to "+tempCit.getTown()+" was sent.");
            return true;
            
        }  else if (split[0].equalsIgnoreCase("accept")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Citizen tempCit = plugin.getCitizen( split[1] ) ;
            if (tempCit == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            if (officer.isOfficer() ){
                if (officer.getTown().equals(tempCit.getTown() ) ) {
                    if ( tempCit.isInvited() ){
                        tempCit.makeMember();
                        player.sendMessage("You have accepted the player");
                    } else { player.sendMessage("That player has not yet been invited");}
                } else {player.sendMessage("Player was not a member of your town"); } 
            } else { player.sendMessage("You are not an officer");}
            player.sendMessage("Invitation to "+tempCit.getTown()+" was sent.");
            return true;
            
        } else if (split[0].equalsIgnoreCase("delete") ||split[0].equalsIgnoreCase("disband")) {
            // This does not but should remove all players from citizens who are members of town
            Town temp = plugin.getTown(player);
            plugin.removeTown(temp.getName() );
            player.sendMessage("Removed: "+ temp.getName() );
            return true;
        }  else if (split[0].equalsIgnoreCase("checkTaxes")) {
            player.sendMessage("Checking taxes in time.  Use the DB for now");
            return true;
        }  else if (split[0].equalsIgnoreCase("setTax")) {
            Town temp = plugin.getTown(player);
            temp.setTaxRate(Double.parseDouble(split[1]) );
            player.sendMessage("You have set the tax rate for "+temp.getName()+ " to "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("kick")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Citizen tempCit = plugin.getCitizen( split[1] ) ;
            if (tempCit == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            if (officer.isOfficer() ){
                if (officer.getTown().equals(tempCit.getTown() ) ) {
                    plugin.removeCitizen(tempCit.getName() );
                    player.sendMessage("The player has been kicked from town");
                } else {player.sendMessage("Player was not a member of your town"); } 
            } else { player.sendMessage("You are not an officer");}
            player.sendMessage("Invitation to "+tempCit.getTown()+" was sent.");
            return true;
            
        } else if (split[0].equalsIgnoreCase("bank")) {
            Town t = plugin.getTown(player);
            player.sendMessage("The town bank has "+ t.getBankBal()+" "+
                    plugin.econwrapper.getCurrName(t.getBankBal()) );
            // withdrawl
            //deposit
            
            return true;
        } else if (split[0].equalsIgnoreCase("deputize")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Citizen tempCit = plugin.getCitizen( split[1] ) ;
            if (tempCit == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            if (officer.isOfficer() ){
                if (officer.getTown().equals(tempCit.getTown() ) ) {
                    tempCit.setDeputy(tempCit.getTown(), true);
                    player.sendMessage("The player has been deputized");
                } else {player.sendMessage("Player was not a member of your town"); } 
            } else { player.sendMessage("You are not an officer");}
            return true;
            
        } else if (split[0].equalsIgnoreCase("resign")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            if (officer.isOfficer() ){
                if (officer.isMayor() ) {
                    player.sendMessage("You cannot resign, find a replacment first");
                    player.sendMessage("*Currently requires a manual database edit*");
                } else {
                    officer.setDeputy (officer.getTown(),false);
                    player.sendMessage("You have become a regular citizen"); } 
            } else { player.sendMessage("You are not an officer");}
            return true;
            
        } else if (split[0].equalsIgnoreCase("rankup")) {
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Town t = plugin.getTown(player);
            if (officer.isMayor() ){
                t.rankup(player);
                player.sendMessage(t.getName()+" ranked to " + t.getRank() );
            } else { player.sendMessage("You are not the mayor"); }
            return true;
            
        } else if (split[0].equalsIgnoreCase("setTax")) {
            if (split.length != 2) {
                player.sendMessage("Not enough parameters;");
                return false;
            }
            Citizen officer = plugin.getCitizen(player.getName() );
            if (officer == null ) {
                player.sendMessage("You are not a member of a town");
                return true;
            }
            Town t = plugin.getTown(player);
            if (officer.isOfficer() ){
                t.setTaxRate(Double.parseDouble( split[1]) );
            } else { player.sendMessage("You are not an officer"); }
            player.sendMessage("Set the tax rate to "+split[1]+" for "+t.getName() );
            return true;
            
        } else {
            displayHelp(player);
            return true;
        }
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
