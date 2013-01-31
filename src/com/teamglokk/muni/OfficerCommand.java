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
            player.sendMessage("Adding a town.");
           //plugin.towns.add(new Town (plugin) );
           // if (plugin.towns.iterator().next().addTown(player, split[1]) ){
           //    return true;
           // } else { return false; }
            return true;
        } else if (split[0].equalsIgnoreCase("test")) { //DELETE MEEEEEE
            player.sendMessage("The command was: "+command.toString() );
            return true;
        } else if (split[0].equalsIgnoreCase("invite")) {
            player.sendMessage("Inviting citizens not yet added.");
            return true;
        } else if (split[0].equalsIgnoreCase("remove") ||split[0].equalsIgnoreCase("disband")) {
            player.sendMessage("Removing a town is not yet added.");
            return true;
        }  else if (split[0].equalsIgnoreCase("checkTaxes")) {
            player.sendMessage("Checking taxes in time.");
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
            player.sendMessage("/deputy regions");
            player.sendMessage("/deputy setRegion");
            player.sendMessage("**/deputy pushBorder");
            player.sendMessage("**/deputy setTax");
            player.sendMessage("**These commands can be run as deputy if the permissions are there");
            player.sendMessage("Mayor command help.  You can do all the above commands with /mayor and these:");
            player.sendMessage("/mayor bank");
            player.sendMessage("/mayor bank deposit");
            player.sendMessage("/mayor bank withdraw");
            player.sendMessage("/mayor deputize");
            player.sendMessage("/mayor resign");
            player.sendMessage("/mayor rankup");
    }
        
   
}
