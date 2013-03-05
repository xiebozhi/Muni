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
package com.teamglokk.muni;
/**
 * TownRanks is used by the Towns class for informational purposes
 * @author shieldsr
 */
public class TownRank {
    protected String rankName = "default";
    protected int rank = 0;
    protected double moneyCost = 100;
    protected int itemCost = 10;
    
    protected int maxDeputies = 5;
    protected int minCitizens = 0;
    protected int maxCitizens = 100;
    
    protected int expansions = 0;
    protected int outposts = 0;
    protected int restaurants = 0;
    protected int hospitals = 0;
    protected int mines = 0;
    protected int embassies = 0;
    protected int arenas = 0;
    
    /**
     * The constructor is the only way to insert data to the class, called from Muni's main class
     * @author bobbshields
     */
    public TownRank (int id, String name, int max_Deputies, int min_Citizens, int max_Citizens, double money_Cost, int item_Cost,
            int expansions, int outposts, int restaurants, int hospitals, int mines, int embassies, int arenas){
        if ( id > 0 ) {rank = id;}
        if (!name.trim().contains("\\s")) {rankName=name; } // disallow spaces in the name
        if (max_Deputies > 0)  { maxDeputies = max_Deputies; }
        if (min_Citizens >= 0) { minCitizens = min_Citizens; }
        if (max_Citizens >= 0) { maxCitizens = max_Citizens; }
        if (money_Cost > 0 ) { moneyCost = money_Cost; }
        if (item_Cost > 0 ) { itemCost = item_Cost; }   
        
        if (expansions >= 0 ) { this.expansions = expansions; }
        if (outposts >= 0 ) { this.outposts = outposts; }    
        if (restaurants >= 0 ) { this.restaurants = restaurants; }    
        if (hospitals >= 0 ) { this.hospitals = hospitals; }    
        if (mines >= 0 ) { this.mines = mines; }    
        if (embassies >= 0 ) { this.embassies = embassies; }     
        if (arenas >= 0 ) { this.arenas = arenas; }     
    }
    
    public String getName(){
        return rankName;
    }
    public int getRank(){
        return rank;
    }
    public int getMaxDeputies(){
        return maxDeputies;
    }
    public int getMinCitizens(){
        return minCitizens;
    }
    public int getMaxCitizens(){
        return maxCitizens;
    }
    public double getMoneyCost(){
        return moneyCost;
    }
    public int getItemCost(){
        return itemCost;
    }
    public int getExpansions(){
        return expansions;
    }
    public int getOutposts(){
        return outposts;
    }
    public int getRestaurants(){
        return restaurants;
    }
    public int getHospitals(){
        return hospitals;
    }
    public int getMines(){
        return mines;
    }
    public int getEmbassies(){
        return embassies;
    }
    public int getArenas(){
        return arenas;
    }
}
