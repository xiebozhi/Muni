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
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;
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
     
    // Stored in (prefix)_citizens	
    private String townMayor;
    protected Citizen mayor = new Citizen(plugin);
    protected TreeMap<String,Citizen> deputies   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> citizens   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> applicants = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> invitees   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    
    //private int maxDeputies = 5;
    
    private Timestamp createdDate;
    /**
     * The citizens who are in this town mapped to their role
     */
    protected HashMap<String,String> citizensMap = new HashMap<String,String>();
    
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
        //loadFromDB(town_Name);
        
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
            citizens.put(c,new Citizen (plugin,townName,c,"citizen",null) );
            plugin.allCitizens.put(c,townName);
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"invitee") ){
            invitees.put(c,new Citizen (plugin,townName,c,"invitee",null) );
            plugin.allCitizens.put(c,townName);
        }
        for (String c :  plugin.dbwrapper.getTownCits(townName,"applicant") ){
            applicants.put(c,new Citizen (plugin,townName,c,"applicant",null) );
            plugin.allCitizens.put(c,townName);
        }
        return true;
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
        for (Citizen curr : citizens.values() ){
            if ( !curr.saveToDB() ) {
                if(plugin.isDebug()){ plugin.getLogger().info("Save failed for citizen: "+curr.getName()+" in "+curr.getTown() ); }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Saves the data for the citizens to the database 
     * @return 
     */
    public boolean saveInvitees(){
        if (citizens.isEmpty() ) {
            if(plugin.isDebug()){ plugin.getLogger().warning("Saving Citizens: Empty list"); }
            return false; 
        }
        for (Citizen curr : invitees.values() ){
            if ( !curr.saveToDB() ) {
                if(plugin.isDebug()){ plugin.getLogger().info("Save failed for citizen: "+curr.getName()+" in "+curr.getTown() ); }
                return false;
            }
        }
        return true;
    }
    
    /**
     * Saves the data for the citizens to the database 
     * @return 
     */
    public boolean saveApplicants(){
        if (citizens.isEmpty() ) {
            if(plugin.isDebug()){ plugin.getLogger().warning("Saving Citizens: Empty list"); }
            return false; 
        }
        for (Citizen curr : applicants.values() ){
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
        if (saveMayor() && saveDeputies() && saveCitizens() && saveInvitees() && saveApplicants() ){
            return true;
        } else { return false; }
    }
    
    /**
     * Gives a string that has all the valid database column names
     * @return 
     */
    public static String toDB_Cols(){
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
     * @deprecated 
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
        for (String c : deputies.keySet() ){
            list = list + c+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no deputies");
        } else {
            plugin.out( player, "Deputies: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (String c : citizens.keySet() ){
            list = list + c+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no citizens");
        } else {
            plugin.out( player, "Citizens: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (String c : invitees.keySet() ){
            list = list + c+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no invitees");
        } else {
            plugin.out( player, "Invitees: "+list.substring(0, list.length()-2));
        }
        list = "";
        
        for (String c : applicants.keySet() ){
            list = list + c+", ";
        }
        if (list.length() == 0){
            plugin.out( player, "There are no applicants");
        } else {
            plugin.out( player, "Applicants: "+list.substring(0, list.length()-2));
        }
    }
      
    /**
     * Messages all the online officers
     * @param msg 
     */
    public void messageOfficers(String msg ) {
        ArrayList<Player> online = new ArrayList<Player>();
        if (plugin.isOnline( mayor.getName() ) ){
            online.add(plugin.getServer().getPlayer(mayor.getName() ) ) ;
        } 
        for (String c : deputies.keySet() ){
            if (plugin.isOnline( c ) ){
                online.add(plugin.getServer().getPlayer( c ) ) ;
            } 
        }
        for (Player p : online ){
            p.sendMessage(msg);
        }
        
    }  
    
    /**
     * Messages all the online players for THIS town
     * @param msg 
     */
    public void announce(String msg ) {
        messageOfficers(msg);
        ArrayList<Player> online = new ArrayList<Player>();
        for (String c : citizens.keySet() ){
            if (plugin.isOnline( c ) ){
                online.add(plugin.getServer().getPlayer( c ) ) ;
            } 
        }
        for (String c : invitees.keySet() ){
            if (plugin.isOnline( c ) ){
                online.add(plugin.getServer().getPlayer( c ) ) ;
            } 
        } 
        for (String c : applicants.keySet() ){
            if (plugin.isOnline( c ) ){
                online.add(plugin.getServer().getPlayer( c ) ) ;
            } 
        }
        for (Player p : online ){
            p.sendMessage(msg);
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
                    mayor = new Citizen ( plugin, townName, player.getName(),"mayor",null );
                    mayor.saveToDB();
                    messageOfficers(mayor.getName() +" has become the mayor of "+townName);
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
            Citizen c = new Citizen (plugin,townName,player.getName(),"citizen",null);
            c.saveToDB();
            mayor.setName("empty");
            admin_makeCitizen(player, player.getName() );
            citizensMap.remove(player.getName() );
            citizensMap.put(player.getName(), "citizen") ;
            announce(player.getName()+" has resigned as mayor");
            plugin.dbwrapper.updateRole(this,player.getName());
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
            Citizen c = new Citizen (plugin,townName,player.getName(),"citizen",null);
            c.saveToDB();
            deputies.remove( player.getName() );
            citizensMap.remove(player.getName() );
            citizensMap.put(player.getName(), "citizen") ;
            announce(player.getName()+" has resigned as deputy");
            plugin.dbwrapper.updateRole(this,player.getName());
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
    public boolean removeCitizen ( String player, Player officer ){
        boolean rtn = false;
        if (!plugin.getTownName(player).equalsIgnoreCase(townName) ){
            officer.sendMessage(player+" is not a member of your town");
            return true;
        }
        if (isDeputy( player) && isMayor( officer.getName() ) ){
            deputies.remove(player);
            // log a transaction here
            rtn = true;
        }
        if (isCitizen( player ) ){
            citizens.remove( player );
            // log a transaction here
            rtn = true;
        } else if (isInvited( player ) ){
            invitees.remove( player );
            // log a transaction here
            rtn = true;
        } else if (isApplicant( player ) ){
            applicants.remove( player );
            // log a transaction here
            rtn = true;
        } 
        if (rtn){
            plugin.dbwrapper.deleteCitizen(player);
            plugin.allCitizens.remove(player);
            announce(player+" was kicked from "+townName+" by "+ officer.getName() );
            if ( plugin.isOnline( player ) ) {
                Player p = plugin.getServer().getPlayer(player);
                p.sendMessage("You have been kicked from "+townName+" by "+ officer.getName() );
            }
        }
        return rtn;
    } 
    
    /**
     * Turns the regular citizen into a deputy, if checks are passed
     * @param player
     * @return 
     */
    public boolean makeDeputy(String player, Player officer){
        if ( isMayor(player) ){
            officer.sendMessage(player +" is already the mayor of "+townName);
            return false;
        } else if ( isDeputy(player ) ){
            officer.sendMessage(player +" is already a deputy of "+townName);
            return false;
        } else if ( isCitizen(player) ){
            if ( getMaxDeputies() > deputies.size() ){ // might have to adjust array...
                //if ( plugin.econwrapper.hasPerm(player, "muni.deputy") || plugin.econwrapper.hasPerm(player, "muni.deputy") ){
                    Citizen c = new Citizen (plugin, townName, player,"deputy", null );
                    c.saveToDB();
                    citizensMap.put(c.getName(), "deputy");
                    citizens.remove(player);
                    deputies.put(player,c );
                    announce(player+" has become a deputy!");
                    plugin.dbwrapper.updateRole(this,player);
                    return true;
                //} else { officer.sendMessage(player+" do not have permission to become a deputy."); }
            } else { officer.sendMessage("Too many deputies, you can only have "+getMaxDeputies()+" deputies."); }
        } else { officer.sendMessage(player+" is not a regular citizen of "+ townName); }
        return false;
    }
    
    /**
     * Officer invites the player into town
     * @param player
     * @param officer
     * @return 
     */
    public boolean invite (String player, Player officer){
        if (!officer.isOnline() ) { return false; }
        if ( !plugin.allCitizens.containsKey(player) ) {
            plugin.allCitizens.put(player, townName);
            Citizen c = new Citizen(plugin,townName,player,"invitee",officer.getName() );
            invitees.put(player, c );
            c.saveToDB();
            return true;
        } else { officer.sendMessage( player+ " is already a member of "+plugin.allCitizens.get(player) ); }
        return false; 
    }
    
    /**
     * The invited player accepts the town invitation
     * @param player
     * @return 
     */
    public boolean acceptInvite(Player player){
        if (!player.isOnline() ) { return false; }
        if (invitees.containsKey(player.getName() ) ){
            citizens.put( player.getName(), new Citizen(plugin,townName,player.getName() ) );
            invitees.remove(player.getName() );
            announce(player.getName()+" has become a town member by invitation");
            plugin.dbwrapper.updateRole(this, player.getName() );
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
        if (!player.isOnline() ) { return false; }
        if (!plugin.allCitizens.containsKey(player.getName() ) ) {
            plugin.allCitizens.put(player.getName(), townName);
            Citizen c = new Citizen(plugin,townName,player.getName(),"applicant",null );
            applicants.put( player.getName(), c );
            messageOfficers(player.getName()+" has applied to be a citizen");
            c.saveToDB();
            return true;
        } else { player.sendMessage("You are already a involved with "+plugin.allCitizens.get(player.getName() ) ); }
        return false; 
    }
    
    /**
     * The officer accepts the application from the player
     * @param player
     * @param officer
     * @return 
     */
    public boolean acceptApplication (String player, Player officer){
        if (!officer.isOnline() ) { return false; }
        if ( plugin.allCitizens.containsKey(player) ) {
            if ( applicants.containsKey ( player ) ){
                citizens.put(player, new Citizen(plugin,townName,player,"citizen", null ) );
                applicants.remove( player );
                announce(player+" has become a town member by application");
                plugin.dbwrapper.updateRole(this, player );
                return true;
            } else { officer.sendMessage( "The player is not an applicant" ); }
        } else { 
            String temp = plugin.allCitizens.get( player );
            if (temp != null){
                officer.sendMessage( "The player is already involved with " + temp ); 
            } else{
                officer.sendMessage( "There is no record for " + player+ ", check the spelling" ); 
            }
        }
        return false;
    }
    
    /**
     * Declines the player's application to this town
     * @param player
     * @return 
     */
    public boolean declineApplication(String player, Player officer ){
        if (!officer.isOnline() ) { return false; }
        if ( applicants.containsKey( player ) ){
            applicants.remove(player );
            plugin.allCitizens.remove( player );
            plugin.dbwrapper.deleteCitizen( player );
            messageOfficers( player+ "'s application has been declined by "+officer.getName() );
            if (plugin.isOnline(player ) ) {
                Player p = plugin.getServer().getPlayer(player);
                p.sendMessage("Your application to "+townName+" has been declined by "+officer.getName() );
            }
            saveToDB();
            return true;
        } else {officer.sendMessage(player+" has not applied to " + townName); }
        return false; 
    }
    
    /**
     * A regular citizen or below of this town can leave
     * @param player
     * @return 
     */
    public boolean leave(Player player){
        if ( plugin.allCitizens.get(player.getName () ).equalsIgnoreCase(townName ) ){
            if ( isCitizen(player) ){
                plugin.allCitizens.remove(player.getName() );
                citizensMap.remove(player.getName() ); 
                citizens.remove( player.getName() );
                announce(player+" has left the town");
                player.sendMessage("You have given up your citizenship to " +townName);
                plugin.dbwrapper.deleteCitizen(player.getName() );
                return true;
            } else {player.sendMessage("You are not a regular citizen of the town.  Officers need to resign first." ); }
        } else {player.sendMessage("You are not in this town"); }
        
        //If we make it to here, see if the player has something pending and clear it
        return clearPending(player);
    }
    
    /**
     * Removes the player from the invite or application collections and from the global list
     * @param player
     * @return 
     */
    public boolean clearPending (Player player) {  
        boolean rtn = false; 
        if ( isInvited(player) ){
            applicants.remove( player.getName() );
            plugin.allCitizens.remove( player.getName() );
            rtn = true;
        }
        if ( isApplicant(player) ){
            invitees.remove( player.getName() );
            plugin.allCitizens.remove( player.getName() );
            rtn = true;
        }
        if (rtn){
            plugin.dbwrapper.deleteCitizen( player.getName() );
            player.sendMessage("Your application or invitation to "+townName+" was cleared");
        }
        return rtn;
    }
    
     /**
     * Forces a player into the mayor role, does not check appropriately
     * @param player
     * @return 
     */
    public boolean admin_makeMayor(String player){
        if ( isMayor(player) ){
            return true;
        }  /*
        if (mayor!=null ){
            if ( !mayor.getName().equalsIgnoreCase("empty") ) { //NPE
            citizens.put(mayor.getName(),mayor);
            plugin.dbwrapper.updateRole(this, mayor.getName() );
            }
        }*/
        plugin.dbwrapper.deleteCitizen(player);
        mayor = new Citizen (plugin, townName, player,"mayor",null );
        mayor.saveToDB();
        return true;
    }
     /**
     * Forces a player into the deputy role, does not check appropriately
     * @param player
     * @return 
     */
    public boolean admin_makeDeputy(String player){
        if ( isMayor(player) || isDeputy (player) ){
            return true;
        } 
        Citizen cit = new Citizen (plugin, townName, player, "deputy", null);
        cit.saveToDB();
        deputies.put(player, cit );
        return true; 
    }
    
    /**
     * Forces a player into the citizen role, does not check appropriately
     * @param player
     * @param officer
     * @return 
     */
    public boolean admin_makeCitizen(CommandSender sender, String player){
        if ( isMayor(player) ||  isDeputy(player ) || isCitizen(player) ) {
            return true;
        } else if ( plugin.allCitizens.containsValue( player ) ) {
            sender.sendMessage( player+ " is a member of a town, deleting membership first!.");
            plugin.allCitizens.remove(player);
            plugin.dbwrapper.deleteCitizen(player);
        } 
        Citizen c = new Citizen (plugin, townName, player, "citizen",null );
        c.saveToDB();
        citizens.put( player, c );
        return true;
    }
    
    /**
     * Forces a player out of the town
     * @param sender
     * @param player
     * @return 
     */
    public boolean admin_removeCitizenship(CommandSender sender, String player){
        boolean rtn = false; 
        if (plugin.isCitizen(townName, player) ){
            if (isMayor(player) ) {
                mayor = new Citizen (plugin, townName, "empty") ;
            }
            if (isDeputy(player) ) {
                deputies.remove(player);
                rtn = true; 
            }if (isCitizen(player) ) {
                citizens.remove(player);
                rtn = true; 
            }if (isInvited(player) ) {
                invitees.remove(player);
                rtn = true; 
            }if (isApplicant(player) ) {
                applicants.remove(player);
                rtn = true; 
            }
            if (rtn) {
                sender.sendMessage( "Citizen " +player+" removed from " +townName);
                plugin.dbwrapper.deleteCitizen(player);
            }
        }
        return rtn; 
    }
    
    /**
     * Returns the appropriate role for the specified player.  Does not check for multiple roles as there should be none
     * @param player
     * @return 
     */
    public String getRole(String player) {
        
        if (mayor.getName().equalsIgnoreCase(player) ) { return "mayor"; } 
        if (deputies.keySet().contains(player) ) { return "deputy"; }
        if (citizens.containsKey(player) ){ return "citizen"; } 
        if (applicants.containsKey(player) ){ return "applicant"; } 
        if (invitees.containsKey(player) ){ return "invitee"; } 
        
        return "nonmember"; 
    }
    
    /**
     * Checks whether player is the mayor of this town
     * @param player
     * @return 
     */
    public boolean isMayor( Player player ){
        return isMayor(player.getName() );
    }
    
    /**
     * Checks whether player is the mayor of this town
     * @param player
     * @return 
     */
    public boolean isMayor( String player ){
        if ( player.equalsIgnoreCase( mayor.getName() ) ){
            return true;
        } else { return false; }
    }
    
    /**
     * Checks whether the player is a deputy of this town
     * @param player
     * @return 
     */
    public boolean isDeputy( Player player ){
        return isDeputy( player.getName() ) ;
    }
    
    /**
     * Checks whether the player is a deputy of this town
     * @param player
     * @return 
     */
    public boolean isDeputy( String player ){
        return deputies.containsKey(player );
    }
    
    /**
     * Checks whether the player is a citizen of this town
     * @param player
     * @return 
     */
    public boolean isCitizen(Player player){
        return isCitizen(player.getName());
    }
    /**
     * Checks whether the player is a citizen of this town
     * @param player
     * @return 
     */
    public boolean isCitizen(String player){
        return citizens.containsKey ( player ) ;
    }
    
    /**
     * Checks whether the player is an officer of this town
     * @param player
     * @return 
     */
    public boolean isOfficer (Player player){
        return isOfficer( player.getName() );
    }
    
    /**
     * Checks whether the player is an officer of this town
     * @param player
     * @return 
     */
    public boolean isOfficer (String player){
        if (isMayor(player) || isDeputy(player) ){
            return true;
        }
        return false; 
    }
    
    /**
     * Checks whether a player is an applicant of this town
     * @param player
     * @return 
     */
    public boolean isApplicant (Player player) {
        return isApplicant(player.getName() );
    }
    
    /**
     * Checks whether a player is an applicant of this town
     * @param player
     * @return 
     */
    public boolean isApplicant (String player) {
        return applicants.containsKey ( player ) ;
    }
    
    /**
     * Checks whether the player is an invitee of this town
     * @param player
     * @return 
     */
    public boolean isInvited (Player player) {
        return isInvited( player.getName() );
    }
    
    /**
     * Checks whether the player is an invitee of this town
     * @param player
     * @return 
     */
    public boolean isInvited (String player) {
        return invitees.containsKey ( player );
    }
        
    /**
     * Removes all the citizens from this town, used when mayor/admin does delete
     */
    public void removeAllTownCits() {
        ArrayList<String> cits = new ArrayList<String>();
        cits.add(mayor.getName() );
        for (String c : deputies.keySet() ){
            cits.add(c);
        }
        for (String c : citizens.keySet() ){
            cits.add(c);
        }
        for (String c : invitees.keySet() ){
            cits.add(c);
        }
        for (String c : applicants.keySet() ){
            cits.add(c);
        }
        for (String c : cits){
            plugin.allCitizens.remove(c); //NPE
            plugin.dbwrapper.deleteCitizen(c);
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
    public boolean rankup(Player mayor){ //needs better output but working - 18 Feb 13
        if (townRank+1 > plugin.getTotalTownRanks() ) {
            mayor.sendMessage("You are already at the highest rank!");
            return true;
        }
        double rankCost = plugin.townRanks[townRank+1].getMoneyCost();
        int rankCostItem = plugin.townRanks[townRank+1].getItemCost();
        
        if ( rankCost <= townBankBal && townRank <= plugin.getTotalTownRanks () ){
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
     * Gets the town rank
     * @return 
     */
    public String getTitle(){
        return plugin.townRanks[townRank].getName();
    }
    
    /**
     * Player deposits funds into the town bank
     * @param player
     * @param amount
     * @return 
     */
    public boolean tb_deposit(Player player, double amount){
        if ( plugin.econwrapper.pay(player,amount,0,"TB Deposit") ){
            townBankBal = townBankBal + amount;
            messageOfficers(player.getName()+" deposited "+amount+" into the town bank");
            return true;
        } else {return false; }
    }
    
    /**
     * Officer withdraws funds from the town bank
     * @param officer
     * @param amount
     * @return 
     */
    public boolean tb_withdraw(Player officer, double amount){
        if ( townBankBal >= amount ){
            if (plugin.econwrapper.giveMoney(officer,amount, "TB Withdraw") ) {
                townBankBal = townBankBal - amount;
            messageOfficers(officer.getName()+" withdrew "+amount+" from the town bank");
                return true;
            } 
        } 
        return false; 
    }
    
    /**
     * Messages the sender the balance of the town bank
     * @param sender 
     */
    public void checkTownBank(CommandSender sender) {
            sender.sendMessage(townName+"'s town bank has a balance of "+ getBankBal()+" "+
                    plugin.econwrapper.getCurrName( getBankBal()) );
    }
    /**
     * Player pays the set amount of taxes
     * @param player
     * @return 
     */
    public boolean payTaxes(Player player){
        return payTaxes(player,taxRate);
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
            player.sendMessage("You have paid your taxes to "+townName+" of amount "+ amount+" "+plugin.econwrapper.getCurrName(amount) );
            messageOfficers(player.getName() +"has paid " +amount+ " in taxes");
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
    
    /**
     * Custom hashcode 
     * @return 
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31). 
            append(townName).toHashCode();
    }
    
    /**
     * Custom equals
     * @param obj
     * @return 
     */
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
    
    /**
     * Custom string
     * @return 
     */
    @Override
    public String toString(){
        return townName;
    }
    
    /**
     * Custom comparison
     * @param t
     * @return 
     */
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
}


