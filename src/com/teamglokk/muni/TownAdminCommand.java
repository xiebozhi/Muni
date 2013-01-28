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

        if (split[0].equalsIgnoreCase("reload") ) {
            player.sendMessage("Reloading config");
            plugin.reloadConfig();
            return true;
        } else if (split[0].equalsIgnoreCase("save")) {
            player.sendMessage("Saving config");
            plugin.saveConfig();
            return true;
        }
        else {
            return false;
        }
    }
    
   
}
