
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
import com.teamglokk.muni.DatabaseFunctions;
/**
 * Handler for the /town command.
 * @author BobbShields
 */
public class TownCommand implements CommandExecutor {
    private Muni plugin;
    private Player player;
    
    public TownCommand (Muni instance){
        plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            return false;
        }
        player = (Player) sender;

        if (split[0].equalsIgnoreCase("add") ) {
            //Location location = player.getLocation();
            // split.length == 0
            player.sendMessage("Adding a town.");
            return true;
        } else if (split[0].equalsIgnoreCase("remove")) {
            try {
                double x = Double.parseDouble(split[0]);
                double y = Double.parseDouble(split[1]);
                double z = Double.parseDouble(split[2]);

                player.teleport(new Location(player.getWorld(), x, y, z));
            } catch (NumberFormatException ex) {
                player.sendMessage("Given location is invalid");
                return true;
            }

        
        } else if (split[0].equalsIgnoreCase("mayor")) {
            player.sendMessage("Mayor .");
            return true;
            
        } else if (split[0].equalsIgnoreCase("check")) {
            
            player.sendMessage("Checking build perms.");
            if (plugin.wgwrapper.checkBuildPerms(player) ){
                
               player.sendMessage("You may build here");
            } else{
                player.sendMessage("You may not build here.");
            }
            
               
            return true;
        } else if (split[0].equalsIgnoreCase("bal")) {
            
            player.sendMessage("Your balance is "+ plugin.econwrapper.getBalance(player));
            
            return true;
        }
        else {
            return false;
        }  
        return false;
    }
    
   
}
