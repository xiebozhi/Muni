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

/**
 * A data container for World Guard Region data, used in the database 
 * @author Bobb
 */
public class MuniWGRegion {
    private String world;
    private String region;
    private String displayName;
    private String type;
    
    public MuniWGRegion(String world, String region, String type) {
        this.world = world;
        this.region = region;
        this.type = type; 
    }
    
    public MuniWGRegion(String world, String region, String displayName, String type) {
        this.world = world;
        this.region = region;
        this.displayName = displayName;
        this.type = type; 
    }
    
    public String getWorld(){
        return world;
    }
    
    public String getRegionName(){
        return region;
    }
    
    public String getDisplayName(){
        return displayName;
    }
    
    public String getType(){
        return type; 
    }
    
    public void setWorld(String w){
        world = w;
    }
    
    public void setRegion(String r){
        region = r; 
    }
    
    public void setDisplayName(String dN) {
        displayName = dN;
    }
    
    public void setType(String t){
        type = t;
    }
}
