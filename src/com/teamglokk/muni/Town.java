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

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.command.CommandSender;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Town implements Comparable<Town> {
    // Gives access to global vars and functions
    private static Muni plugin;
    
    // Database table is $db_prefix_ towns
    //private int db_pk;
    private String townName;
    //private Location townCenter;   
    private double townBankBal;
    private double taxRate;
    private int townRank;
     
	// Stored in (prefix)_citizens	TreeSet<Citizen> citizens = new TreeSet<Citizen>();
    private String townMayor;
    protected Citizen mayor = new Citizen(plugin);
    protected HashMap<String,Citizen> deputies = new HashMap<String,Citizen>();
    protected TreeSet<Citizen> citizens = new TreeSet<Citizen>();
    protected TreeSet<Citizen> applicants = new TreeSet<Citizen>();
    protected TreeSet<Citizen> invitees = new TreeSet<Citizen>();
    
    //private int maxDeputies = 5;
    
    private Timestamp createdDate;
    /**
     * The citizens who are in this town mapped to their role
     */
    //protected HashMap<String,roles> citizensMap = new HashMap<String,roles>();
    
    /**
     * Default constructor with no data
     * @param instance 
     */
    public Town (Muni instance){
        plugin = instance;
    }
    
    /**
     * Copy constructor
     * @param copy 
     */
    public Town (Town copy){
        townName = copy.getName();
        townMayor = copy.getMayor(); 
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        taxRate = copy.getTaxRate();
        mayor = copy.mayor;
        deputies = copy.deputies;
        citizens = copy.citizens;
        applicants = copy.applicants;
        invitees = copy.invitees;
    }
    
    /**
     * Constructor for starting a new town with specified player as the mayor
     * @param instance
     * @param town_Name
     * @param player 
     */
    public Town (Muni instance, String town_Name, String player ){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Town with mayor: "+town_Name+", "+player);
        townName = town_Name;
        townMayor = player; 
        townRank = 1;
        townBankBal = 5;
        taxRate = 10;
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }
    /**
     * Full data constructor
     * 
     * @author bobbshields
     */
    public Town (Muni instance, String town_Name, String mayor,
            int rank, double bankBal, double tax){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Begin Muni Constructor: "+mayor+", "+town_Name);
        townName = town_Name;
        townMayor = mayor; 
        this.mayor = new Citizen (plugin, mayor);
        townRank = rank;
        townBankBal = bankBal;
        taxRate = tax;
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }
    /* Loads the class instance variables from the database using the passed town name.
     * 
     * @author bobbshields
     */
    public Town (Muni instance, String town_Name ){
        
        plugin = instance;
        loadFromDB(town_Name);
        
    }    
    
    /**
     * Loads the town data from the database
     * @param town_Name
     * @return 
     */
    public boolean loadFromDB(String town_Name){
        Town copy = plugin.dbwrapper.getTown(town_Name);
        //plugin = copy.plugin;
        townMayor = copy.getMayor(); 
        townName = copy.getName();
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        taxRate = copy.getTaxRate();
        
        mayor = new Citizen (plugin, townName, copy.getMayor(),"mayor",null );
        plugin.allCitizens.put(copy.getMayor(),townName);
        
         if (!plugin.dbwrapper.checkExistence("citizens", "townName", townName) ){
            if (plugin.isDebug()){ plugin.getLogger().warning("No citizens found for "+townName)  ;}
            return false;
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"deputy") ){
            deputies.put(c, new Citizen (plugin,townName,c,"deputy",null) );
            plugin.allCitizens.put(c,townName);
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"citizen") ){
            citizens.add(new Citizen (plugin,townName,c,"citizen",null) );
            plugin.allCitizens.put(c,townName);
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"invitee") ){
            invitees.add(new Citizen (plugin,townName,c,"invitee",null) );
            plugin.allCitizens.put(c,townName);
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"applicant") ){
            applicants.add(new Citizen (plugin,townName,c,"applicant",null) );
            plugin.allCitizens.put(c,townName);
        }
        return true;
    }    
    public String getRole(String player) {
        
        if (mayor.getName().equalsIgnoreCase(player) ) { return "mayor"; } 
        if (deputies.keySet().contains(player) ) { return "deputy"; }
        if (citizens.contains(new Citizen (plugin,townName, player) ) ){ return "citizen"; } 
        if (applicants.contains(new Citizen (plugin,townName, player) ) ){ return "applicant"; } 
        if (invitees.contains(new Citizen (plugin,townName, player) ) ){ return "invitee"; } 
        
        return "nonmember"; 
    }
    
    /**
     * Saves the town data to the database
     * @return 
     */
    public boolean saveToDB(){
        // if exists, update; else insert
        boolean temp = false; 
        if ( plugin.dbwrapper.checkExistence("towns", "townName", townName) ){
            temp =  plugin.dbwrapper.updateRow("towns", "townName", townName, toDB_UpdateRowVals());
        } else {
            temp =  plugin.dbwrapper.insert("towns", toDB_Cols(), toDB_Vals() );
            
        }
        if (plugin.isDebug()){ plugin.getLogger().warning("Looking for me? Saving citizens next"); }
        saveAllCitizens();
        return temp; 
    }
    /**
     * Saves the data for the mayor to the database 
     * @return 
     */
    public boolean saveMayor(){
        return mayor.saveToDB();
    }
    
    /**
     * Saves the data for the deputies to the database 
     * @return 
     */
    public boolean saveDeputies(){
        if (deputies.isEmpty() ) {
            if(plugin.isDebug()){ plugin.getLogger().warning("Saving Deputies: Empty list"); }
            return false; 
        }
        
        for (Citizen curr : deputies.values() ){
            if ( !curr.saveToDB() ) {
                if(plugin.isDebug()){ plugin.getLogger().info("Save failed for deputy: "+curr.getName()+" in "+curr.getTown() ); }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Saves the data for the citizens to the database 
     * @return 
     */
    public boolean saveCitizens(){
        if (citizens.isEmpty() ) {
            if(plugin.isDebug()){ plugin.getLogger().warning("Saving Citizens: Empty list"); }
            return false; 
        }
        for (Citizen curr : citizens){
            if ( !curr.saveToDB() ) {
                if(plugin.isDebug()){ plugin.getLogger().info("Save failed for citizen: "+curr.getName()+" in "+curr.getTown() ); }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Saves all the citizens to the database
     * @return 
     */
    public boolean saveAllCitizens(){
        if (saveMayor() && saveDeputies() && saveCitizens() ){
            return true;
        } else { return false; }
    }
    
    /**
     * Gives a string that has all the valid database column names
     * @return 
     */
    public String toDB_Cols(){
        return "townName,mayor,townRank,bankBal,taxRate";
    }
    
    /**
     * Gives a string that has all the values for this town, follows order of toDB_Cols()
     * @return 
     */
    public String toDB_Vals(){
        return "'"+townName +"','"+townMayor+"','"+
               Integer.toString(townRank) +"','"+
               Double.toString(townBankBal) +"','"+ Double.toString(taxRate)+"'";
    }  
    
    /**
     * Gives special string to allow a database update to the whole row in the wrapper
     * @return 
     */ 
    public String toDB_UpdateRowVals(){
        return "townName='"+townName+"', townRank='"+townRank+"', bankBal='"+
                Double.toString(townBankBal)+"', taxRate='"+Double.toString(taxRate)+"' ";
    }
    
    /**
     * Gives a string of user-friendly information about the town
     * @return 
     */
    public String info(){
        return toDB_Vals();
    }
    /**
     * Gives a string of user-friendly information about the town
     * @return 
     */
    public void info(CommandSender player){
        plugin.out( player, townName+" is a "+plugin.townRanks[townRank].getName() );
        plugin.out( player, "The town bank balance is "+townBankBal+" and the tax rate is "+taxRate+".");
        listAllCitizens(player);
    }
    /**
     * Gives a string of user-friendly information about the town
     * @return 
     */
    public void listAllCitizens(CommandSender player){
        String list = "";
        plugin.out( player, "Mayor: "+mayor.getName() );
        for (Citizen c : deputies.values() ){
            list = list + c.getName()+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no deputies");
        } else {
            plugin.out( player, "Deputies: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (Citizen c : citizens ){
            list = list + c.getName()+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no members");
        } else {
            plugin.out( player, "Citizens: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (Citizen c : invitees ){
            list = list + c.getName()+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no invitees");
        } else {
            plugin.out( player, "Invitees: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (Citizen c : applicants ){
            list = list + c.getName()+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no applicants");
        } else {
            plugin.out( player, "Applicants: "+list.substring(0, list.length()-2));
        }
    }
            
    /**
     * Makes the player into the town mayor, if checks are passed
     * @param player
     * @return 
     */
    public boolean makeMayor(Player player){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a mayor of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return true;
        }
        if ( isDeputy(player) || isCitizen(player) ){
            if (mayor.getName().equalsIgnoreCase( "empty" ) ){
               if ( plugin.econwrapper.hasPerm( player, "muni.mayor" ) ){
                    mayor = new Citizen ( plugin, townName, player.getName() );
                    saveToDB();
                    return true;
                } else { player.sendMessage("You do not have permission to become a mayor."); }
            } else {player.sendMessage("There is already a mayor") ;}
        } else {player.sendMessage("You're not a citizen of the town" ); }
        return true;
    }
    
    /**
     * Makes the mayor into a regular citizen then opens mayor slot
     * @param player
     * @return 
     */
    public boolean resignMayor (Player player){
        if (isMayor(player ) ) {
            mayor.setName("empty");
            makeCitizen(player,player);
            // log a transaction here
            return true;
        } else {
            player.sendMessage("You are not the mayor, so you can't stop being mayor");
            return false;
        }
    }
    /**
     * Makes the deputy into a regular citizen
     * @param player
     * @return 
     */
    public boolean resignDeputy ( Player player ){
        if ( isDeputy(player) ){
            deputies.remove(new Citizen (plugin,player) );
            saveDeputies();
            // log a transaction here
            return true;
        } else {
            player.sendMessage("You are not a deputy, so you can't stop being one");
            return false;
        }
    }
    
    /**
     * Kicks the citizen from the town
     * @param player
     * @return 
     */
    public boolean removeCitizen ( Player player, Player officer ){
        if (isCitizen( player ) ){
            citizens.remove(new Citizen (plugin,player) );
            saveCitizens();
            // log a transaction here
            return true;
        } else { return false; }
    }
    
    /**
     * Turns the regular citizen into a deputy, if checks are passed
     * @param player
     * @return 
     */
    public boolean makeDeputy(Player player, Player officer){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a deputy of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return false;
        } else if ( isDeputy(player ) ){
            player.sendMessage("You are already a deputy of "+townName);
            return false;
        } else if ( isCitizen(player) ){
            if ( getMaxDeputies() > deputies.size() ){ // might have to adjust array...
                if ( plugin.econwrapper.hasPerm(player, "muni.deputy")
                        || plugin.econwrapper.hasPerm(player, "muni.deputy") ){
                    deputies.put(player.getName(),new Citizen (plugin, townName, player.getName() ) );
                    saveToDB();
                    return true;
                } else { player.sendMessage("You do not have permission to become a deputy."); }
            } else { player.sendMessage("Too many deputies, rank up or fire someone"); }
        } else { player.sendMessage("You are not a member of "+ townName); }
        return false;
    }
    
    /**
     * Turns the player into a regular citizen, if not citizen elsewhere
     * @param player
     * @param officer
     * @return 
     */
    public boolean makeCitizen(Player player, Player officer){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a citizen of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return false;
        } else if ( isDeputy(player ) ){
            player.sendMessage("You are already a deputy of "+townName);
            return false;
        } else if ( plugin.allCitizens.containsValue( player.getName() ) ) {
            player.sendMessage("You are already a member of a town.");
            return false;
        } else if ( getMaxCitizens() > deputies.size() + citizens.size() ){ // might have to adjust array...
                if ( plugin.econwrapper.hasPerm(player, "muni.citizen") ){
                    citizens.add(new Citizen (plugin, townName, player.getName() ) );
                    //saveCitizens();
                    return true;
                } else { player.sendMessage("You do not have permission to become a citizen."); }
        } else { player.sendMessage(townName+" has too many citizens.  Wait for a vacancy"); }
        return false;
    }
    
    /**
     * Officer invites the player into town
     * @param player
     * @param officer
     * @return 
     */
    public boolean invite (Player player, Player officer){
        if (!plugin.allCitizens.containsKey(player.getName() ) ) {
            plugin.allCitizens.put(player.getName(), townName);
            invitees.add(new Citizen(plugin,townName,player.getName(),"invitee",officer.getName() ) );
            return true;
        } else { player.sendMessage("You are already a member of "+plugin.allCitizens.get(player.getName() ) ); }
        return false; 
    }
    
    /**
     * The invited player accepts the town invitation
     * @param player
     * @return 
     */
    public boolean acceptInvite(Player player){
        if (invitees.contains(new Citizen(plugin,townName,player.getName() ) ) ){
            //plugin.allCitizens.put(player.getName(), townName);
            citizens.add(new Citizen(plugin,townName,player.getName() ) );
            invitees.remove(new Citizen(plugin,townName,player.getName() ) );
            //need to make changes to the database here
            return true;
        } else {player.sendMessage("You have not been invited to " + townName); }
        return false; 
    }
    
    /**
     * Declines the player's application to this town
     * @param player
     * @return 
     */
    public boolean declineApplication(Player player){
        if (applicants.contains(new Citizen(plugin,townName,player.getName() ) ) ){
            applicants.remove(new Citizen(plugin,townName,player.getName() ) );
            plugin.allCitizens.remove( player.getName() );
            //need to make changes to the database here
            return true;
        } else {player.sendMessage("You have not been invited to " + townName); }
        return false; 
    }
    
    /**
     * Player applies to be accepted into town
     * @param player
     * @return 
     */
    public boolean apply (Player player) {
        if (!plugin.allCitizens.containsKey(player.getName() ) ) {
            plugin.allCitizens.put(player.getName(), townName);
            applicants.add(new Citizen(plugin,townName,player.getName(),"applicant",null ) );
            return true;
        } else { player.sendMessage("You are already a involved with "+plugin.allCitizens.get(player.getName() ) ); }
        return false; 
    }
    
    /**
     * Removes the player from the invite or application collections and from the global list
     * @param player
     * @return 
     */
    public boolean clearPending (Player player) {
        if (!plugin.allCitizens.containsKey(player.getName() ) ){
            player.sendMessage("You are not a member any town" );
            return false;
        }
        if (!plugin.allCitizens.get(player.getName() ).equalsIgnoreCase(townName) ){
            player.sendMessage("You are not a member of "+townName);
            return false;
        }
        boolean rtn = false; 
        if ( isInvited(player) ){
            applicants.remove(new Citizen (plugin,townName,player.getName() ));
            plugin.allCitizens.remove( player.getName() );
            rtn = true;
        }
        if ( isApplicant(player) ){
            invitees.remove(new Citizen (plugin,townName,player.getName() ));
            plugin.allCitizens.remove( player.getName() );
            rtn = true;
        }
        return rtn;
    }
    
    /**
     * 
     * @param player
     * @return 
     */
    public boolean isApplicant (Player player) {
        if (applicants.contains(new Citizen (plugin,townName,player.getName() ) ) ){
            return true;
        } else { return false; }
    }
    public boolean isInvited (Player player) {
        if (invitees.contains(new Citizen (plugin,townName,player.getName() ) ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * The officer accepts the application from the player
     * @param player
     * @param officer
     * @return 
     */
    public boolean acceptApplication (Player player, Player officer){
        if (invitees.contains(new Citizen(plugin,townName,player.getName() ) ) ){
            citizens.add(new Citizen(plugin,townName,player.getName() ) );
            applicants.remove(new Citizen(plugin,townName,player.getName() ) );
            //need to make changes to the database here
            return true;
        } else {officer.sendMessage("The player is already involved with " + plugin.allCitizens.get(player.getName() ) ); }
        return false; 
    }
    
    public boolean leave(Player player){
        if (plugin.allCitizens.get(player.getName () ).equalsIgnoreCase(townName ) ){
            if (isCitizen(player) ){
                citizens.remove(new Citizen (plugin,townName,player.getName() ) );
                return true;
            } else {player.sendMessage("You are not a citizen of the town " ); }
        } else {player.sendMessage("You are not a member of this town"); }
        return false; 
    }
    
    /**
     * Checks whether player is the mayor of this town
     * @param player
     * @return 
     */
    public boolean isMayor( Player player ){
        if ( player.getName().equalsIgnoreCase( mayor.getName() ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * Checks whether the player is a deputy of this town
     * @param player
     * @return 
     */
    public boolean isDeputy( Player player ){
        if (deputies.containsKey(player.getName() ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * Checks whether the player is a citizen of this town
     * @param player
     * @return 
     */
    public boolean isCitizen(Player player){
        if (citizens.contains( new Citizen(plugin, player.getName()) ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * Loads all citizens from the database into the town citizen collections
     */
    /*
    public void loadCitizens(){
        try{
            Iterator itr = plugin.dbwrapper.getTownCits( townName ).iterator();
            if ( plugin.isDebug() ) { plugin.getLogger().info(" Loading Citizens. " ); }
            while ( itr.hasNext() ){
                String current = itr.next().toString();
                if ( plugin.isDebug() ) { plugin.getLogger().info("Loading citizens: " + current); }
                citizens.add( new Citizen( plugin, current ) ); //constructor loads from DB
                if ( citizens.last().isMayor() ) {
                    mayor = citizens.last();
                    citizens.remove(citizens.last() );
                }
                if ( citizens.last().isDeputy() ) {
                    deputies.put(citizens.last().getName(),citizens.last() );
                    citizens.remove(citizens.last() );
                }
          }
        } catch (NullPointerException ex){
            plugin.getLogger().severe("Failed to load citizens for town ("+ townName +"): "+ex.getMessage() );
        } finally {
            if ( plugin.isDebug() ) { plugin.getLogger().info("Finshed loading Citizens"); }
        }
    }*/
    
    /**
     * Adds the town to the database
     * @param mayor
     * @param town_Name
     * @return 
     */
    public boolean db_addTown(Player mayor, String town_Name){
        if ( !plugin.econwrapper.payMoney(mayor,1000) ){
            mayor.sendMessage("Not enough money to found the town");
            return false;
        }
        townName = town_Name;
        townMayor = mayor.getName(); 
        townBankBal = 0;
        taxRate = 10;
        
        if (!plugin.dbwrapper.checkExistence("towns","townName", townName) ) {
            plugin.dbwrapper.insert("towns",toDB_Cols(),toDB_Vals() );
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Adds this town to the database
     * @return 
     */
    public boolean db_addTown(){
        if (!plugin.dbwrapper.checkExistence("towns","townName", townName) ) {
            plugin.dbwrapper.insert("towns",toDB_Cols(),toDB_Vals() );
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Gets the maximum number of deputies defined for this townRank in the config
     * @return 
     */
    public int getMaxDeputies() {
        return plugin.townRanks[townRank].getMaxDeputies(); 
    }
    
    /**
     * Gets the maximum number of citizens defined for this townRank in the config
     * @return 
     */
    public int getMaxCitizens() {
        return plugin.townRanks[townRank].getMaxCitizens(); 
    }
    
    /**
     * Gets the mayor's name
     * @return 
     */
    public String getMayor(){
        return mayor.getName() ;
    }
    
    /**
     * Gets a string of deputies, comma separated 
     * @return 
     */
    public String getDeputies(){
        String temp = null;
        for (Citizen d: deputies.values() ){
             temp = temp + d.getName() +", ";
         }
         return temp;
    }
    
    /**
     * Check whether the player is the mayor or a deputy
     * @param player
     * @return 
     */
    public boolean checkOfficer (Player player) {
        if (isMayor(player) || isDeputy(player) ) {
            return true;
        } else { return false; } 
    }
    
    /**
     * Mayor ranks up the town after checks and payment
     * @param player
     * @return 
     */
    public boolean rankup(Player mayor){
        double rankCost = plugin.townRanks[townRank+1].getMoneyCost();
        int rankCostItem = plugin.townRanks[townRank+1].getItemCost();
        
        if ( rankCost >= townBankBal ){
            if (plugin.econwrapper.payItemR( mayor, plugin.rankupItemID, rankCostItem,"Rankup" ) ) {
                mayor.sendMessage("You have successfully ranked "+ townName +" to level " + (++townRank) );
                return payFromTB(rankCost); //Payment of money taken after sponges are confirmed
            } else{ 
                mayor.sendMessage("You do not have enough "+"Sponges"+" in your inventory to rank up "+ townName);
                return false;
            }
        }else {
            mayor.sendMessage("You need to depsoit "+ (rankCost-townBankBal) +" into the town bank to rank up "+ townName);
            return false;
        }  
    }
    
    /**
     * Withdraw from the town bank
     * @param amount
     * @return 
     */
    public boolean payFromTB (Double amount){
        if ( townBankBal >= amount ){
                townBankBal = townBankBal - amount;
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Gets the town rank
     * @return 
     */
    public int getRank(){
        return townRank;
    }
    
    /**
     * Player deposits funds into the town bank
     * @param player
     * @param amount
     * @return 
     */
    public boolean tb_deposit(Player player, double amount){
        if ( plugin.econwrapper.payMoney(player,amount) ){
            townBankBal = townBankBal + amount;
            return true;
        } else {return false; }
    }
    
    /**
     * Officer withdraws funds from the town bank
     * @param officer
     * @param amount
     * @return 
     */
    public boolean tb_withdrawl(Player officer, double amount){
        if ( townBankBal >= amount ){
            if (plugin.econwrapper.giveMoney(officer,amount) ) {
                townBankBal = townBankBal - amount;
                return true;
            } else { return false; }
        } else { return false; }
    }
    
    /**
     * Player pays the set amount of taxes
     * @param player
     * @return 
     */
    public boolean payTaxes(Player player){
        if (payTaxes(player, taxRate ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * Player pays a specified amount in taxes
     * @param player
     * @param amount
     * @return 
     */
    public boolean payTaxes(Player player, Double amount){
        if ( plugin.econwrapper.pay(player, amount, 0, "Taxes for "+townName ) ){
            townBankBal = townBankBal + amount;
            //player.sendMessage("You have paid your taxes to "+townName+" of amount "+ amount+" "+plugin.econwrapper.getCurrName(amount) );
            //Transaction t = new Transaction ( )
            return true;
        } else { return false; }
    }
    
    /**
     * Checks the town bank balance
     * @return 
     */
    public double getBankBal (){
        return townBankBal;
    }
    
    /**
     * Checks the current tax rate
     * @return 
     */
    public double getTaxRate(){
        return taxRate;
    }
    
    /**
     * Officer sets the current tax rate
     * @param rate
     * @return 
     */ // need to add player to the parameters and check for officership
    public boolean setTaxRate(double rate){
        // verify the rate is above 0 and below the hard max
        if (rate < plugin.maxTaxRate && rate > 0){
            taxRate = rate;
            return true;
        } else{
            return false;
        }
    }
    
    /**
     * Gets the town name
     * @return 
     */
    public String getName(){
        return townName;
    }
    
    /**
     * Sets the town name
     * @param name
     * @return 
     */
    public boolean setName(String name){
        townName = name;
        return true;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31). 
            append(townName).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        } else if ( toString().equals(obj.toString() ) ) {
            return true;
        } else if(obj.getClass() != getClass()){
            return false;
        } else { return false;}
    }
    @Override
    public String toString(){
        return townName;
    }
    @Override
    public int compareTo(Town t){
        return this.getName().toLowerCase().compareTo( t.getName().toLowerCase() );
        /*
        if (this.getRank() < t.getRank() ){
            return -1;
        } else if (this.getRank() > t.getRank() ){
            return 1;
        } else if (this.getRank() == t.getRank() ){
            return this.getName().toLowerCase().compareTo( t.getName().toLowerCase() );
        } else { return 0;}
        */
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
      
    }*/
}
