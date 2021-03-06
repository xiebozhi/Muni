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

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import org.bukkit.util.Vector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.databases.RegionDBUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;
import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    /**
     * Reminder to make this function in the future
     * @param worldName
     * @param regionName
     * @return 
     */
    public boolean createSchematic (String worldName, String regionName){
        //may have to add an instance of world edit to accomplish this... not sure yet
        return true;
    }
    /**
     * Deletes a region 
     * @param regionName
     * @return 
     */
    public boolean deleteRegion (String worldName, String regionName) {
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        
        if (!mgr.hasRegion(regionName)) {
            return false;
        }
        
        mgr.removeRegion(regionName);

        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            plugin.getLogger().warning("Failed to delete region: "  + e.getMessage() );
        }
        return true; 
    }
    
    /**
     * Deletes the parent and the subregions of the given town
     * @param town 
     */
    public void deleteAllRegions(Town town) {
        
        List<MuniWGRegion> subRegions = plugin.dbwrapper.getSubRegions(town);
        if (plugin.isDebug() ) { plugin.getLogger().info("Deleting: "+town.getName()+"Subregions"); }
        for ( MuniWGRegion wgr : subRegions ){
            if (plugin.isDebug() ) { plugin.getLogger().info("Deleting: "+wgr.getRegionName()+" on world: "+wgr.getWorld()); }
            this.deleteRegion(wgr.getWorld(), wgr.getRegionName());
        }
        this.deleteRegion( town.getWorld(), town.getName() );
    }
    
    /**
     * This will be used to check a square area for other regions
     * @param loc
     * @param radius
     * @param excludedRegions
     * @return 
     */
    public boolean checkSquareArea (Location loc, int radius, List<String> excludedRegions){
        
        return true; 
    }
    /**
     * This will be used to check in a direction
     * @param loc1
     * @param loc2
     * @param dir
     * @param excludedRegions
     * @return 
     */
    public boolean checkDirection (Location loc1, Location loc2, String dir, int dist, List<String> excludedRegions){
        Set<ProtectedRegion> regionSet = new HashSet<ProtectedRegion>();
        ApplicableRegionSet regions = null;
        
        if ( loc1.getWorld() != loc2.getWorld() ) { return false; }
        // needing to verify the two locations are on a line
        
        // Figure out the step based on chosen direction
        Location adjustment = new Location ( loc1.getWorld(), 0, 0, 0 );
                if ( dir.equalsIgnoreCase("n") || dir.equalsIgnoreCase("north") ){
            adjustment.add(0, 0, dist);
        } else if ( dir.equalsIgnoreCase("s") || dir.equalsIgnoreCase("south") ){
            adjustment.add(0, 0, -dist);
        } else if ( dir.equalsIgnoreCase("e") || dir.equalsIgnoreCase("east") ){
            adjustment.add(dist, 0, 0);
        } else if ( dir.equalsIgnoreCase("w") || dir.equalsIgnoreCase("west") ){
            adjustment.add(-dist, 0, 0);
        } else { return false; }
        
        Location currenLoc = loc1;
        regions = getARS(loc1);
        
        // At some point we iterate between the line endpoints and note all the regions
        
        // Add the found regions to the set (no duplicates) 
        for (ProtectedRegion r : regions){
            regionSet.add(r);
        }
        
        for (String eR : excludedRegions){
            //regionSet.
        }
        
        return true; 
    }
    
    
    /**
     * Expands the specified region in one direction by the rules defined in config
     * @return 
     */
    public int expandRegion ( String worldName, String regionName,
            String dir, int expansion) {
        
        if (expansion <= 0 ) { return -1; }
        
        plugin.getLogger().severe("World name: "+worldName);
        World world = plugin.getServer().getWorld( worldName );
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        final ProtectedRegion existing = mgr.getRegion( regionName );
        
        BlockVector min = existing.getMinimumPoint();
        BlockVector max = existing.getMaximumPoint();
        int x1 = min.getBlockX();
        int y1 = min.getBlockY() <= 0 ? 0 : min.getBlockY() ;
        int z1 = min.getBlockZ();
        int x2 = max.getBlockX();
        int y2 = max.getBlockY() >= world.getMaxHeight() ? world.getMaxHeight() : max.getBlockY();
        int z2 = max.getBlockZ();
        plugin.getLogger().info(min.toString() );
        plugin.getLogger().info(max.toString() );
        
        
        if ( dir.equalsIgnoreCase("n") || dir.equalsIgnoreCase("north") ){
            z1 = z1 - expansion;
        } else if ( dir.equalsIgnoreCase("s") || dir.equalsIgnoreCase("south") ){
            z2 = z2 + expansion;
        } else if ( dir.equalsIgnoreCase("e") || dir.equalsIgnoreCase("east") ){
            x2 = x2 + expansion;
        } else if ( dir.equalsIgnoreCase("w") || dir.equalsIgnoreCase("west") ){
            x1 = x1 - expansion;
        } else if ( dir.equalsIgnoreCase("u") || dir.equalsIgnoreCase("up") ){
            y2 = y2 + expansion;
            if (y2 > world.getMaxHeight() ) {return -2; }
        } else if ( dir.equalsIgnoreCase("d") || dir.equalsIgnoreCase("down") ){
            y1 = y1 - expansion;
            if (y1 < 0 ) {return -2; }
        } else { return -1; }
        
        BlockVector newMin = new BlockVector(x1,y1,z1);
        BlockVector newMax = new BlockVector(x2,y2,z2);
        
        plugin.getLogger().info(newMin.toString() );
        plugin.getLogger().info(newMax.toString() );
        
        int area = (x2 - x1) * (z2 - z1);
        
        ProtectedRegion newRegion = new ProtectedCuboidRegion(regionName, newMin, newMax);
            
        newRegion.setMembers(existing.getMembers());
        newRegion.setOwners(existing.getOwners());
        newRegion.setFlags(existing.getFlags());
        newRegion.setPriority(existing.getPriority());
        try {
            newRegion.setParent(existing.getParent());
        } catch (CircularInheritanceException ignore) {
        }

        mgr.addRegion(newRegion);
        
       try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            plugin.getLogger().warning("Failed to write region: "  + e.getMessage() );
        }
        return area;
    } 
    
    /**
     * Checks with the region manager to see if the region exists
     * @param world
     * @param name
     * @return 
     */
    public boolean isExistingRegion (World world, String name) {
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        if ( mgr.hasRegion(name) ){
            return true; 
        } else { return false; }
    }
    
    /**
     * Makes a square region of a specified size 
     * @param town
     * @param player
     * @param subRegionName
     * @return 
     */
    public ProtectedRegion makeSubRegion (Town town, Player player, String subRegionName, 
            int halfSize, int height ){
        return makeSubRegion(town,player,subRegionName,halfSize,height,null,null,false,false,false);
    }
    
    /**
     * Makes a subregion of the town with special flags set
     * @param town
     * @param player
     * @param subRegionName
     * @param halfSize
     * @param height
     * @param greeting
     * @param farewell
     * @param pvp
     * @param heal
     * @param feed
     * @return 
     */
    public ProtectedRegion makeSubRegion (Town town, Player player, String subRegionName, 
            int halfSize, int height, String greeting, String farewell, boolean pvp,
            boolean heal, boolean feed){
        
        World townWorld = plugin.getServer().getWorld( town.getWorld() );
        RegionManager mgr = wg.getGlobalRegionManager().get( townWorld );
        ProtectedRegion parent = mgr.getRegion( town.getName() );
        ProtectedRegion child = null;
            
        if ( isExistingRegion (townWorld, subRegionName) ) {
            player.sendMessage("That region alreay exists"); 
            return null;
        }
        if ( makeRegion (player.getLocation(),subRegionName,halfSize,height,false) > 0 ){
            mgr = wg.getGlobalRegionManager().get( player.getWorld() );
            child = mgr.getRegion( subRegionName );
            setParent(parent,child);
        }
        
        child.setFlag(DefaultFlag.GREET_MESSAGE, greeting);
        child.setFlag(DefaultFlag.FAREWELL_MESSAGE, farewell);
        if (pvp){ 
            child.setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW); 
        } else {
            child.setFlag(DefaultFlag.PVP, StateFlag.State.DENY); 
        }
        if (heal){ 
            child.setFlag(DefaultFlag.HEAL_AMOUNT, 1); 
            child.setFlag(DefaultFlag.HEAL_DELAY, 1); 
        }
        if (feed){ 
            child.setFlag(DefaultFlag.FEED_AMOUNT, 1); 
            child.setFlag(DefaultFlag.FEED_DELAY, 1); 
        }
        
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            plugin.getLogger().warning("Failed to write region: "  + e.getMessage() );
        }
        return child;
    }
    
    /**
     * Makes a 25x(vert)x25 region centered on the player
     * @param player
     * @param regionName
     * @return 
     */
    public int makeTownBorder ( Player player, Town town ) {
         RegionManager mgr = wg.getGlobalRegionManager().get(player.getWorld());
         if (!ProtectedRegion.isValidId(town.getName() ) ) {
            player.sendMessage("Region cannot be named: " +town.getName()+" (INVALID)" ) ;
            return -1; 
        }
        if (town.getName().equalsIgnoreCase( "__global__" )){
            player.sendMessage("The region cannot be named __global__" ) ;
            return -1; 
        }
        if (mgr.hasRegion(town.getName())) {
            player.sendMessage( "There is already a region by that name" );
            return -1;
        }
        int rtn =  makeRegion (player.getLocation(), town.getName(), 12, 10, false);
        
        this.makeOwners(town.getWorld(), town.getName(), town.getOfficerList());
        this.makeMembers(town.getWorld(), town.getName(), town.getRegCitsList());
        
        return rtn; 
    }
    
    /**
     * Future planning: highlight with wool or glowstone along the ground at the border
     * may only replace grass, dirt, and stone along that path
     * needs to be a temporary function (client side if possible)
     * 
     * @param town
     * @return 
     */
    public boolean highlightTownBorder( Town town, int height ) {
        
        World world = plugin.getServer().getWorld( town.getWorld() );
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        final ProtectedRegion existing = mgr.getRegion( town.getName() );
        
        BlockVector min = existing.getMinimumPoint();
        BlockVector max = existing.getMaximumPoint();
        int x1 = min.getBlockX();
        int y1 = height;
        int z1 = min.getBlockZ();
        int x2 = max.getBlockX();
        int y2 = height;
        int z2 = max.getBlockZ();
        
        
        
        return true; 
    }
    
    /**
     * Makes a region that centered on the player
     * @param player
     * @param regionName
     * @return 
     */
    public int makeRegionVert ( Location loc, String regionName, int halfSize ) {
        return makeRegion(loc, regionName,halfSize,0,true);
    }
    
    /**
     * Makes a square region
     * @param loc
     * @param regionName
     * @param halfSize half the size of the square (halfSize = 12 => region area = 25*25)
     * @param height how tall the new region should be (above the passed location)
     * @param vert if true, height is ignored
     * @return 
     */
    public int makeRegion ( Location loc, String regionName, int halfSize, int height , boolean vert) {
        if (!ProtectedRegion.isValidId( regionName ) ) {
            return -1; 
        }
        if (regionName.equalsIgnoreCase( "__global__" )){
            return -1; 
        }
        
        //Prepare the first corner
        Vector shift = new Vector (halfSize,0,halfSize);
        Location loc1 = loc.clone();
        loc1.add(shift);
        
        //Prepare the second corner
        shift = new Vector (-halfSize,0,-halfSize);
        Location loc2 = loc.clone();
        loc2.add(shift);
        
        //Decide on the height
        if (vert){
            loc1.setY(loc.getWorld().getMaxHeight() );
            loc2.setY(0);
        } else {
            loc1.add( 0, height, 0 );
            loc2.add( 0, -12, 0 );
        }
        
        ProtectedRegion newRegion;
        CuboidSelection sel = new CuboidSelection(loc.getWorld(),loc1,loc2);
        BlockVector min = sel.getNativeMinimumPoint().toBlockVector();
        BlockVector max = sel.getNativeMaximumPoint().toBlockVector();
        newRegion = new ProtectedCuboidRegion(regionName, min, max);
            
        RegionManager mgr = wg.getGlobalRegionManager().get(loc.getWorld());
        
        if (mgr.hasRegion(regionName)) {
            return -1;
        }
        mgr.addRegion(newRegion);
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            plugin.getLogger().warning("Failed to write region: "  + e.getMessage() );
        }
        return sel.getArea(); 
    }
    
    /**
     * Makes a personal region where only the player is the owner
     * @param player
     * @return 
     */
    public boolean makeHome (Player player){
        // Check that the player doesn't have one first
        if (this.isExistingRegion(player.getWorld(), player.getName() ) ){
            return false; 
        }
        // Check that the player is currently in the town border
        ApplicableRegionSet here = getARS(player.getLocation());
        String townName = plugin.getTownName( player.getName() );
        boolean result = false; 
        Iterator<ProtectedRegion> itr = here.iterator();
        while ( itr.hasNext() ) {
            if ( itr.next().getId().equalsIgnoreCase( townName ) ) {
                result = true; 
            }
        }
        // Make the region with a set size
        if (result){
            this.makeRegion(player.getLocation(), player.getName(), 5, 30, false);
            List<String> temp = new ArrayList<String>();
            temp.add(player.getName());
            this.makeOwners(player.getWorld().getName(),player.getName(), temp);
            return true; 
        } 
        return false;
    }
    
    /**
     * Makes a personal region with flags set for a shop
     * @param player
     * @return 
     */
    public boolean makeShop (Player player) { 
        // Check that the player doesn't have one first
        if (this.isExistingRegion(player.getWorld(), player.getName()+"_shop" ) ){
            return false; 
        }
        // Check that the player is currently in the town border
        ApplicableRegionSet here = getARS(player.getLocation());
        String townName = plugin.getTownName( player.getName() );
        boolean result = false; 
        Iterator<ProtectedRegion> itr = here.iterator();
        while ( itr.hasNext() ) {
            if ( itr.next().getId().equalsIgnoreCase( townName ) ) {
                result = true; 
            }
        }
        // Make the region with a set size
        if (result){
            this.makeRegion(player.getLocation(), player.getName()+"_shop", 5, 10, false);
            List<String> temp = new ArrayList<String>();
            temp.add(player.getName()+"_shop");
            this.makeOwners(player.getWorld().getName(),player.getName()+"_shop", temp);
            // Set flags
            this.setFlag(this.getRegion(player.getWorld().getName(),player.getName()+"_shop"),DefaultFlag.CHEST_ACCESS,true);
            return true; 
        } 
        return false;
    }
    
    /**
     * Makes a subregion of the town where only the officers can build
     * @param town
     * @param player
     * @param subRegionName
     * @return 
     */
    public boolean makeGovernment (Town town, Player player){
       // Check that the town doesn't have one first
        if (this.isExistingRegion(player.getWorld(), town.getName()+"_government" ) ){
            return false; 
        }
        // Check that the player is currently in the town border
        ApplicableRegionSet here = getARS(player.getLocation());
        boolean result = false; 
        Iterator<ProtectedRegion> itr = here.iterator();
        while ( itr.hasNext() ) {
            if ( itr.next().getId().equalsIgnoreCase(town.getName() ) ) {
                result = true; 
            }
        }
        // Make the region with a set size
        if (result){
            this.makeRegion(player.getLocation(), town.getName()+"_government", 12, 30, false);
            this.makeOwners(player.getWorld().getName(),town.getName()+"_government", town.getOfficerList() );
            // Set flags
            return true; 
        } 
        return false;
    }
    
    /**
     * Makes a PVP-safe zone outside of any border that has the town as its primary region
     * @param town
     * @param player
     * @param subRegionName
     * @param SRdisplayName
     * @return 
     */
    public boolean makeOutpost (Town town, Player player, String subRegionName) {
        boolean rtn = true;
        ProtectedRegion region = makeSubRegion(town,player,subRegionName,12,40);
        plugin.dbwrapper.addSubRegion(town.getWorld(), town.getName(), subRegionName, "outpost");
        
        // require outside of any protections 
        
        return rtn;
    }
    
    /**
     * Makes a food regen region only inside of the town's border
     * @param town
     * @param player
     * @param subRegionName
     * @param SRdisplayName
     * @return 
     */
    public boolean makeRestaurant (Town town, Player player, String subRegionName) {
        boolean rtn = true;
        makeSubRegion(town,player,subRegionName,7,10,"Welcome to the restaurant","You are leaving the restaurant",false,false,true);
        plugin.dbwrapper.addSubRegion(town.getWorld(), town.getName(), subRegionName, "restaurant");
        
        // check inside main town
        // set food regen
        
        return rtn;
    }
    
    /**
     * Makes a health regen region only inside of the town's border
     * @param town
     * @param player
     * @param subRegionName
     * @param SRdisplayName
     * @return 
     */
    public boolean makeHospital (Town town, Player player, String subRegionName) {//, String SRdisplayName) {
        boolean rtn = true;
        makeSubRegion(town,player,subRegionName,7,10,"Welcome to the Hospital","You are leaving the hospital",false,true,false);
        plugin.dbwrapper.addSubRegion(town.getWorld(), town.getName(), subRegionName, "hospital");
        // check inside main town
        // set heath regen
        
        return rtn;
    }
    
    /**
     * Makes a region inside a foreign town protection that is only buildable by the original town's members
     * @param town
     * @param player
     * @param subRegionName
     * @param SRdisplayName
     * @return 
     */
    public boolean makeEmbassy (Town town, Player player, String subRegionName) {
        boolean rtn = true;
        makeSubRegion(town,player,subRegionName,12,50,"Welcome to the embassy","You are leaving the embassy",false,false,false);
        plugin.dbwrapper.addSubRegion(town.getWorld(), town.getName(), subRegionName, "embassy");
        // require inside of another town's main protection (no embassy in an outpost)
        
        return rtn;
    }
    
    /**
     * Makes a PVP region only inside of the town's border
     * @param town
     * @param player
     * @param subRegionName
     * @param SRdisplayName
     * @return 
     */
    public boolean makeArena (Town town, Player player, String subRegionName) {
        boolean rtn = true;
        makeSubRegion(town,player,subRegionName,37,50,"Welcome to the arena floor","You are leaving the arena floor",true,false,false);
        plugin.dbwrapper.addSubRegion(town.getWorld(), town.getName(), subRegionName, "arena");
        // check inside main town
        // set PVP
        
        return rtn;
    }
    /**
     * Takes a list of players and ensures they are members of a region
     * @param worldName
     * @param regionName
     * @param players
     * @return 
     */
    public boolean makeMembers(String worldName, String regionName, List<String> players){
        if (players.size() == 0 ) {return false; } 
        
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        ProtectedRegion region = mgr.getRegion(regionName);

        if (region == null) {
            plugin.getLogger().info("Could not find a region called: "+regionName);
        }

        String [] newMembers = new String[players.size()];
        int i = 0;
        
        for (String localplayer : players ){
            if (!region.isMember(localplayer) ){
                newMembers[i++] = localplayer;
            }        
        }
        
        if (newMembers.length != 0) {
            RegionDBUtil.addToDomain(region.getMembers(), newMembers, 0);
        } else { return true; } //all listed members are already members
        
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            throw new CommandException("Failed to write regions: "
                    + e.getMessage());
        }
        return true; 
    }
    
    /**
     * Removes the listed players from the protection specified.
     * @param worldName
     * @param regionName
     * @param players
     * @return 
     */
    public boolean removeFromProt(String worldName,String regionName,List<String> players){
        if (players.size() == 0 ) {return false; } 
        
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        ProtectedRegion region = mgr.getRegion(regionName);

        if (region == null) {
            plugin.getLogger().info("Could not find a region called: "+regionName);
        }

        String [] MembersToBeRemoved = new String[players.size()];
        String [] OwnersToBeRemoved = new String[players.size()];
        int i = 0;
        
        for (String localplayer : players ){
            if (region.isMember(localplayer) ){
                MembersToBeRemoved[i++] = localplayer;
            }        
            if (region.isOwner(localplayer) ){
                OwnersToBeRemoved[i++] = localplayer;
            }     
        }
        
        if (MembersToBeRemoved.length != 0) {
            RegionDBUtil.removeFromDomain(region.getMembers(), MembersToBeRemoved, 0);
        } else { return true; } //all listed players are not in the domain
        if (OwnersToBeRemoved.length != 0) {
            RegionDBUtil.removeFromDomain(region.getOwners(), OwnersToBeRemoved, 0);
        } else { return true; } //all listed players are not in the domain
            
        
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            throw new CommandException("Failed to write regions: "
                    + e.getMessage());
        }
        return true; 
    }
    
    /**
     * Takes a list of players and ensures they are owners of a region
     * @param worldName
     * @param regionName
     * @param players
     * @return 
     */
    public boolean makeOwners(String worldName, String regionName, List<String>players){
        if (players.size() == 0 ) {return false; } 
        
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        ProtectedRegion region = mgr.getRegion(regionName);

        if (region == null) {
            plugin.getLogger().info("Could not find a region called: "+regionName);
        }

        String [] newOwners = new String[players.size()];
        int i = 0;
        
        for (String localplayer : players ){
            if (!region.isOwner(localplayer) ) {
                newOwners[i++] = localplayer;
            }
        }
        if (newOwners.length != 0) {
            RegionDBUtil.addToDomain(region.getOwners(), newOwners, 0);
        } else {return true;} // all listed owners are already owners
        
        try {
            mgr.save();
        } catch (ProtectionDatabaseException e) {
            throw new CommandException("Failed to write regions: "
                    + e.getMessage());
        }
        return true; 
    }
    
    /**
     * Establishes the parent / child relationship
     * @param child
     * @param parent
     * @return 
     */
    public boolean setParent (ProtectedRegion parent, ProtectedRegion child){
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
    
    /**
     * Returns the valid region from the world guard manager
     * @param worldName
     * @param regionName
     * @return 
     */
    public ProtectedRegion getRegion (String worldName, String regionName){
        ProtectedRegion rtn = null;
        World world = plugin.getServer().getWorld(worldName);
        RegionManager mgr = wg.getGlobalRegionManager().get( world );
        
        rtn = mgr.getRegion(regionName);
        
        return rtn;
    }    
    
    /**
     * Checks the World Guard build permission at the player's current location
     * @param player
     * @return 
     */
    public boolean checkBuildPerms (Player player)
    {
        return this.wgp.canBuild(player,
            player.getLocation().getBlock().getRelative(0, -1, 0));
    }
    
    /**
     * Gets the regions where the player is standing
     * @param player
     * @return 
     */
    public ApplicableRegionSet getARS(Location loc) {
        return plugin.wgp.getRegionManager( loc.getWorld() )
                .getApplicableRegions(loc );
    }

   /**
    * Gets whether the player can use the specified flag at his/her location
    * @param player
    * @param flag
    * @return 
    */
    public boolean isFlagged (Location loc, StateFlag flag ){
        return getARS( loc ).allows(flag);
    }
    
    /**
     * Sets the flag for the region 
     * @param region
     * @param flag
     * @param value
     * @return 
     */
    public void setFlag (ProtectedRegion region, Flag flag, String value){
            //Check that the user has ownership
            region.setFlag(flag,value );
        
    }
    
    /**
     * Sets the flag for the region 
     * @param region
     * @param flag
     * @param value
     * @return 
     */
    public void setFlag (ProtectedRegion region, Flag flag, boolean value){
            //Check that the user has ownership
            region.setFlag(flag,value );
        
    }
    
    /**
     * Sets the boolean flag for the region 
     * @param region
     * @param flag
     * @param value
     * @return 
     */
    public boolean setflag (ProtectedRegion region, Flag flag, boolean value){
        region.setFlag(flag, value);
        return true;
    }
    
    /**
     * Sets the Entry message for the region 
     * @param region
     * @param message
     * @return 
     */
    public boolean setMSG_Entry(ProtectedRegion region,String message){
        setFlag(region, DefaultFlag.GREET_MESSAGE, message);
        return true;
    }
    
    /**
     * Sets the Exit message for the region 
     * @param region
     * @param message
     * @return 
     */
    public boolean setMSG_Exit(ProtectedRegion region, String message) {
        setFlag(region, DefaultFlag.GREET_MESSAGE, message);
        return true; 
    }
    
    /**
     * Sets the PVP flag for the region to the value of the toggle parameter
     * @param region
     * @param toggle
     * @return 
     */
    public boolean setPVP(ProtectedRegion region, boolean toggle){
        
        return true; 
    }
    
    /**
     * Sets the health regeneration of the region to a predetermined value 
     * @param region
     * @return 
     */
    public boolean setRegen_Health(ProtectedRegion region){
        
        return true; 
    }
    
    /**
     * Sets the food regeneration of the region to a predetermined value 
     * @param region
     * @return 
     */
    public boolean setRegen_Food(ProtectedRegion region){
        
        return true; 
    }
    
    /**
     * Sets all the flags to a basic state
     * @param region
     * @return 
     */
    public boolean resetToStandardFlags(ProtectedRegion region){
        
        return true; 
    }

}
