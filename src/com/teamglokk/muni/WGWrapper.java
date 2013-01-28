package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
//import static com.sk89q.worldguard.bukkit.BukkitUtil.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import org.bukkit.entity.Player;
import org.bukkit.Location;
/**
 * Makes the World Guard Commands easier to work with
 * @author BobbShields
 */
public class WGWrapper extends Muni {
    private final Muni plugin;
    private WorldGuardPlugin wg = null;
    private RegionManager rm = null;
        
    public WGWrapper(Muni instance) {
        plugin = instance;
        //wg =  plugin.wgp.; 
    }
  
    boolean expandRegion ( /*Player.location() player,*/) { 

/*
            ProtectedRegion town = getRegion("test");

            region.setFlag(flag, flag.parseInput(plugin, sender, value))

            ? parseInput(WorldGuardPlugin plugin, CommandSender sender, String input)
            //where ? is the relevant data type for that flag

            //WorldGuardPlugin worldGuard = getWorldGuard();
            //Vector pt = toVector(block); // This also takes a location

            RegionManager regionManager = worldGuard.getRegionManager(world);
            ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
            return set.canBuild(localPlayer);
        */
        return true;
    } 
    boolean checkBuildPerms (Player player)
    {
        return this.wgp.canBuild(player,
            player.getLocation().getBlock().getRelative(0, -1, 0));
    }
    String getRegions (Player player)
    {
        //return this.wgp.getRegionManager(null).
         //       canBuild(player,
          //  player.getLocation().getBlock().getRelative(0, -1, 0));
        return "nothing";
    }
   ApplicableRegionSet getARS(Player player) {
        return plugin.wgp.getRegionManager( player.getWorld() )
                .getApplicableRegions(player.getLocation() );
    }

   //requires if string.equals("pvp") { getflag(DefaultFlag.PVP }; elseif...
    boolean getflag (Player player, StateFlag flag ){
        //region.getFlag(flag, flag.parseInput(plugin.wgp, player, flag));
        return getARS(player).allows(flag);
    }
    
    boolean setflag (Player player, ProtectedRegion region,
                Flag flag, String value){
            //Check that the user has ownership
            region.setFlag(flag,value );
            return true;
        
    }
    
    boolean setflag (Player player, ProtectedRegion region,
                Flag flag, boolean value){
        region.setFlag(flag, value);
        return true;
    }

}
