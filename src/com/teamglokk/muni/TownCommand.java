
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
            player.sendMessage("Remove.");
            return true;
        } else if (split[0].equalsIgnoreCase("tp")) {
            player.sendMessage("TP to pos.");
            try {
                double x = Double.parseDouble( split[1] );
                double y = Double.parseDouble( split[2] );
                double z = Double.parseDouble( split[3] );

                player.teleport(new Location(player.getWorld(), x, y, z));
            } catch (NumberFormatException ex) {
                player.sendMessage("Given location is invalid");
                return true;
            }
        }else if (split[0].equalsIgnoreCase("mayor")) {
            player.sendMessage("Mayor.");
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
        }else if (split[0].equalsIgnoreCase("pay")) {
            double amount = Double.parseDouble(split[1]) ;
            if (plugin.econwrapper.pay(player, amount)){
                player.sendMessage("You paid " + amount  );
            }
            
            return true;
        }else if (split[0].equalsIgnoreCase("list")) {
            player.sendMessage("List.");
            return true;
        }else if (split[0].equalsIgnoreCase("info")) {
            player.sendMessage("Info.");
            return true;
        }else if (split[0].equalsIgnoreCase("join")) {
            player.sendMessage("Join.");
            return true;
        }else if (split[0].equalsIgnoreCase("leave")) {
            player.sendMessage("Leave.");
            return true;
        }else if (split[0].equalsIgnoreCase("admin")) {
            player.sendMessage("Admin.");
            return true;
        }else if (split[0].equalsIgnoreCase("help")) {
            player.sendMessage("Muni Help.  You can do these commands:");
            player.sendMessage("/muni list");
            player.sendMessage("/muni info");
            player.sendMessage("/muni join");
            player.sendMessage("/muni add");
            player.sendMessage("/muni remove");
            return true;
        }else if (split[0].equalsIgnoreCase("testperms")) {
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
        }
        else {
            return false;
        }
        return false;
    }
    
   
}
