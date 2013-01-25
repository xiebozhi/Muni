/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni;

import org.bukkit.Location;

/**
 *
 * @author Bobb
 */
public class Town {
    
    //private Muni plugin;
    
    private String townName;
    private int townRank;
    private double townBankBal;
    private String [] citizens;  
    private String [] invitees;
    private String [] applicants;
    private double taxRate;
    private Location townCenter;
    private double [] rankupMoneyCost;
    private double [] rankupItemCost;
    
    public Town (){
        
    }
    
    public String getName(){
        return townName;
    }
    public boolean setName(String name){
        townName = name;
        return true;
    }
}
