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

        if (split[0].equalsIgnoreCase("help") || split[0].isEmpty() ) {
            player.sendMessage("Muni Help.  You can do these commands:");
            player.sendMessage("/muni list");
            player.sendMessage("/muni info");
            player.sendMessage("/muni join");
            player.sendMessage("/muni add");
            player.sendMessage("/muni remove");
            player.sendMessage("/muni sethome");
            player.sendMessage("/muni vote");
            player.sendMessage("/muni payTaxes");
            return true;
        } else if (split[0].equalsIgnoreCase("payTaxes")) {
            player.sendMessage("Preparing to pay taxes.");
            return true;
        } else if (split[0].equalsIgnoreCase("list")) {
            player.sendMessage("List.");
            return true;
        } else if (split[0].equalsIgnoreCase("info")) {
            player.sendMessage("Info.");
            return true;
        } else if (split[0].equalsIgnoreCase("apply")) {
            player.sendMessage("Apply to a town... but not yet");
            return true;
        } else if (split[0].equalsIgnoreCase("accept")) {
            player.sendMessage("Accept an invite to a town... but not yet");
            return true;
        } else if (split[0].equalsIgnoreCase("leave")) {
            player.sendMessage("Leaving town.");
            return true;
        }else if (split[0].equalsIgnoreCase("sethome")) {
            player.sendMessage("Sethome not yet added.");
            return true;
        }else if (split[0].equalsIgnoreCase("vote")) {
            player.sendMessage("Voting not yet added.");
            return true;
        } else if (split[0].equalsIgnoreCase("checkBank")) {
            player.sendMessage("The bank balance is ");
            return true;
        }  else if (split[0].equalsIgnoreCase("signCharter")) {
            player.sendMessage("Charters not yet accepted ");
            return true;
        } else {
            return false;
        }
        //return false;
    }
    
   
}
