package com.teamglokk.muni.utilities;

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
import com.teamglokk.muni.Muni;
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
  
    public boolean expandRegion ( /*Player.location() player,*/) { 

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
    public boolean checkBuildPerms (Player player)
    {
        return this.wgp.canBuild(player,
            player.getLocation().getBlock().getRelative(0, -1, 0));
    }
    public String getRegions (Player player)
    {
        //return this.wgp.getRegionManager(null).
         //       canBuild(player,
          //  player.getLocation().getBlock().getRelative(0, -1, 0));
        return "nothing";
    }
   public ApplicableRegionSet getARS(Player player) {
        return plugin.wgp.getRegionManager( player.getWorld() )
                .getApplicableRegions(player.getLocation() );
    }

   //requires if string.equals("pvp") { getflag(DefaultFlag.PVP }; elseif...
    public boolean getflag (Player player, StateFlag flag ){
        //region.getFlag(flag, flag.parseInput(plugin.wgp, player, flag));
        return getARS(player).allows(flag);
    }
    
    public boolean setflag (Player player, ProtectedRegion region,
                Flag flag, String value){
            //Check that the user has ownership
            region.setFlag(flag,value );
            return true;
        
    }
    
    public boolean setflag (Player player, ProtectedRegion region,
                Flag flag, boolean value){
        region.setFlag(flag, value);
        return true;
    }

}
