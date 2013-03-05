/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni.utilities;

/**
 *
 * @author shieldsr
 */
public class MuniWGRegion {
    private String world;
    private String region;
    private String type;
    
    public MuniWGRegion(String world, String region, String type) {
        this.world = world;
        this.region = region;
        this.type = type; 
    }
    
    public String getWorld(){
        return world;
    }
    public String getRegionName(){
        return region;
        
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
    public void setType(String t){
        type = t;
    }
    
    
    
}
