/* 
 * Muni 
 * Copyright (C) 2013 bobbshields <https://github.com/xiebozhi/Muni> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Binary releases are available freely at <http://dev.bukkit.org/server-mods/muni/>.
*/
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
