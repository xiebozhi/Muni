package com.teamglokk.muni;

import java.util.Arrays;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.Set;
import java.util.Iterator;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Town {
    
    private static Muni plugin;
    
	// Stored in (prefix)_towns
    private String townName;
    //private Location townCenter;   
    private double townBankBal;
    private double taxRate;
    private int townRank;
     
	// Stored in (prefix)_citizens	
    private String townMayor;
    private int maxDeputies = 5;
    private Set<String> townDeputies = null;
    private Set<String> citizens;  
    private Set<String> invitees;
    private Set<String> applicants;
    
    //private int rankupItemID = 19;
    //use plugin.rankupItemID instead
    
    public String toString_dbCols(){
        return "townName,townRank,bankBal,taxRate";
    }
    public String toString_dbVals(){
        return townName +","+ Integer.toString(townRank) +","+
               Double.toString(townBankBal) +","+ Double.toString(taxRate);
    }
	
    public Town (Muni instance){
        plugin = instance;
        
    }
    public Town (Muni instance, Player player, String townName ){
        plugin = instance;
        
        
    }
    public boolean addTown(Player mayor, String town_Name){
        if ( !plugin.econwrapper.pay(mayor,1000) ){
            mayor.sendMessage("Not enough money to found the town");
            return false;
        }
        townName = town_Name;
        townMayor = mayor.getName();
        townBankBal = 0;
        taxRate = 10;
        
        if (!plugin.dbwrapper.checkExistence("towns","townName", townName) ) {
        plugin.dbwrapper.db_insert("towns",toString_dbCols(),toString_dbVals() );
        return true;
        } else {
            return false;
        }
    }
    public boolean setMaxDeputies(int max){
        maxDeputies = max;
        return true;
    }
    public int getMaxDeputies() {
        return maxDeputies; 
    }
    public String getMayor(){
        return townMayor;
    }
    public boolean setMayor(String mayor){
        townMayor = mayor;
        return true;
    }
    public String getDeputies(){
        String temp = null;
        Iterator<String> itr = townDeputies.iterator();
         while(itr.hasNext() ){
             temp = temp + itr.next() +", ";
         }
         return temp;
    }
    public boolean addDeputy(String deputy){
        townDeputies.add(deputy);
        return true;
    }
    public boolean checkOfficer (String player) {
        if (townMayor.equalsIgnoreCase(player) )
        { return true;
        } else if (Arrays.asList(townDeputies).contains(player) ){
            return true;
        } else {return false;}
    }
    public boolean rankup(Player player){
        double rankCost = plugin.townRanks[townRank+1].getMoneyCost();
        int rankCostItem = plugin.townRanks[townRank+1].getItemCost();
        
        if ( rankCost >= townBankBal ){
            if (plugin.econwrapper.pay_item( player, plugin.rankupItemID, rankCostItem ) ) {
                player.sendMessage("You have successfully ranked "+ townName +" to level " + (++townRank) );
                return payFromTB(rankCost); //Payment of money taken after sponges are confirmed
            } else{ 
                player.sendMessage("You do not have enough "+"Sponges"+" in your inventory to rank up "+ townName);
                return false;
            }
        }else {
            player.sendMessage("You need to depsoit "+ (rankCost-townBankBal) +" into the town bank to rank up "+ townName);
            return false;
        }  
    }
    public boolean payFromTB (Double amount){
        if ( townBankBal >= amount ){
                townBankBal = townBankBal - amount;
            return true;
        }else{
            return false;
        }
    }
    public int getRank(){
        return townRank;
    }
    public boolean setRank(int rank){
        if (rank >= 0 && rank < 10){
            townRank = rank;
            return true;
        }else{ return false; }
    }
    public boolean tb_deposit(Player player, double amount){
        if ( plugin.econwrapper.pay(player,amount) ){
            townBankBal = townBankBal + amount;
            return true;
        } else {return false; }
    }
    public boolean tb_withdrawl(Player player, double amount){
        if (plugin.econwrapper.giveMoney(player,amount) ) {
            townBankBal = townBankBal - amount;
            return true;
        } else { return false; }
    }
    public double tb_balance (){
        return townBankBal;
    }
    public double getTaxRate(){
        return taxRate;
    }
    public boolean setTaxRate(double rate){
        // verify the rate is above 0 and below the hard max
        if (rate < plugin.maxTaxRate && rate > 0){
            taxRate = rate;
            return true;
        } else{
            return false;
        }
    }
    public String getName(){
        return townName;
    }
    public boolean setName(String name){
        townName = name;
        return true;
    }
        /*
        public Location getCenter (){
        return townCenter;
    }
    public boolean setCenter (Location center ){
        if (center != null) {
            townCenter = center;
            return true;
        } else {return false;}
    }  
    public boolean setRankupMoneyCosts(double [] costs){
        rankupMoneyCost = costs;
        return true;
    }
    public boolean setRankupItemCosts(int [] costs){
        rankupItemCost = costs;
        return true;
    } */ 
    /*
    public double [] getRankupMoneyCosts(){
        return rankupMoneyCost;
    }    
    public double getRankupMoneyCost(int rank){
        return rankupMoneyCost[rank];
    }
    public int [] getRankupItemCosts(){
        return rankupItemCost;
    }    
    public int getRankupItemCost(int rank){
        return rankupItemCost[rank];
    }*/
}
