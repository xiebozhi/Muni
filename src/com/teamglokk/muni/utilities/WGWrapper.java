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

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.util.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
//import static com.sk89q.worldguard.bukkit.BukkitUtil.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.RegionDBUtil;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.teamglokk.muni.Muni;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandException;
/**
 * Makes the World Guard Commands easier to work with
 * @author BobbShields
 */
public class WGWrapper extends Muni {
    private final Muni plugin;
    private WorldGuardPlugin wg = null;
    private RegionManager rm = null;
        
    /**
     * Default constructor
     * @param instance 
     */
    public WGWrapper(Muni instance) {
        plugin = instance;
        wg =  plugin.wgp; 
    }
    
    //https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/bukkit/commands/RegionCommands.java
    //https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/bukkit/commands/RegionMemberCommands.java
    //https://github.com/sk89q/worldedit/blob/master/src/main/java/com/sk89q/worldedit/commands/SelectionCommands.java
    //http://docs.sk89q.com/worldguard/apidocs/
    
    /**
     * Deletes a region 
     * @param regionName
     * @return 
     */
    public boolean deleteRegion (String regionName) {
        
        return true; 
    }
    /**
     * Makes a region that belongs to the player
     * @param player
     * @param regionName
     * @return 
     */
    public boolean makeRegion ( Player player, String regionName ) {
        if (!ProtectedRegion.isValidId(regionName ) ) {
            player.sendMessage("Region cannot be named: " +regionName+" (INVALID)" ) ;
            return false; 
        }
        if (regionName.equalsIgnoreCase("__global__")){
            player.sendMessage("The region cannot be named __global__" ) ;
            return false; 
        }
        RegionManager mgr = wg.getGlobalRegionManager().get(player.getWorld());
        if (mgr.hasRegion(regionName)) {
            player.sendMessage("There is already a region by that name" );
            return false;
        }
        ProtectedRegion region;
        int sa = 10;
        
        Vector shift = new Vector (sa,0,sa);
        Location loc1 = player.getLocation().add(shift);
        loc1.setY(player.getWorld().getMaxHeight() );
        shift = new Vector (-sa,0,-sa);
        Location loc2 = player.getLocation().add(shift);
        loc2.setY(0);
        
        CuboidSelection sel = new CuboidSelection(player.getWorld(),loc1,loc2);
        
        
        BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
        BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
        region = new ProtectedCuboidRegion(regionName, min, max);
            
        mgr.addRegion(region);

        try {
            mgr.save();
            player.sendMessage(ChatColor.YELLOW + "Region saved as " + regionName + ".");
        } catch (ProtectionDatabaseException e) {
            player.sendMessage("Failed to write region: "  + e.getMessage() );
        }
        return true; 
    }
    
    public boolean makeMembers(String worldName, String regionName, List<String> players){
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        ProtectedRegion region = mgr.getRegion(regionName);

        if (region == null) {
            plugin.getLogger().info("Could not find a region called: "+regionName);
        }

        String [] newMembers = new String[players.size()];
        int i = 0;
        
        for (String localplayer : players ){
            newMembers[i++] = localplayer;
            
        }
        
        RegionDBUtil.addToDomain(region.getMembers(), newMembers, 0);

        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            throw new CommandException("Failed to write regions: "
                    + e.getMessage());
        }
        return true; 
    }
    
    public boolean makeOwners(String worldName, String regionName, List<String>players){
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        ProtectedRegion region = mgr.getRegion(regionName);

        if (region == null) {
            plugin.getLogger().info("Could not find a region called: "+regionName);
        }

        String [] newOwners = new String[players.size()];
        int i = 0;
        
        for (String localplayer : players ){
            newOwners[i++] = localplayer;
        }
        
        RegionDBUtil.addToDomain(region.getOwners(), newOwners, 0);
        
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            throw new CommandException("Failed to write regions: "
                    + e.getMessage());
        }
        return true; 
    }
    
    public boolean setParent (ProtectedRegion child, ProtectedRegion parent){
        try{
            child.setParent(parent);
            return true;
        } catch (CircularInheritanceException e){
            plugin.getLogger().severe("Could not set "+parent.getId()+" as parent region for " +child.getId() );
            plugin.getLogger().severe(e.getMessage() );
            return false; 
        }
    }
    
    public String getParent (ProtectedRegion child){
        return child.getParent().getId();
    }
    
    public ProtectedRegion getRegion (String regionName){
        ProtectedRegion rtn = null;
        
        return rtn;
    }
    /**
     * Gets a list of regions at the player's current location 
     * @param player
     * @return 
     */
    public String getRegions (Player player)
    {
        //return this.wgp.getRegionManager(null).
         //       canBuild(player,
          //  player.getLocation().getBlock().getRelative(0, -1, 0));
        return "nothing";
    }
    
    
    /**
     * Expands the specified region in one direction by the rules defined in config
     * @return 
     */
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
    
    /**
     * Checks the World Guard build permisison at the player's current location
     * @param player
     * @return 
     */
    public boolean checkBuildPerms (Player player)
    {
        return this.wgp.canBuild(player,
            player.getLocation().getBlock().getRelative(0, -1, 0));
    }
    
   public ApplicableRegionSet getARS(Player player) {
        return plugin.wgp.getRegionManager( player.getWorld() )
                .getApplicableRegions(player.getLocation() );
    }

   //requires if string.equals("pvp") { getflag(DefaultFlag.PVP }; elseif...
   /**
    * Gets whether the specified region has the specified flag
    * @param player
    * @param flag
    * @return 
    */
    public boolean getflag (Player player, StateFlag flag ){
        //region.getFlag(flag, flag.parseInput(plugin.wgp, player, flag));
        return getARS(player).allows(flag);
    }
    
    /**
     * Sets the flag for the region 
     * @param player
     * @param region
     * @param flag
     * @param value
     * @return 
     */
    public boolean setflag (Player player, ProtectedRegion region,
                Flag flag, String value){
            //Check that the user has ownership
            region.setFlag(flag,value );
            return true;
        
    }
    
    /**
     * Sets the boolean flag for the region 
     * @param player
     * @param region
     * @param flag
     * @param value
     * @return 
     */
    public boolean setflag (Player player, ProtectedRegion region,
                Flag flag, boolean value){
        region.setFlag(flag, value);
        return true;
    }

}
