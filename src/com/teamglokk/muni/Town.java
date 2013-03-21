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

import com.teamglokk.muni.utilities.Transaction;
import java.sql.Timestamp;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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
    private Location townCenter;   
    private double townBankBal;
    private double taxRate;
    private int townBankItemBal;
    private int taxItemRate;
    private int townRank;
    private String townWorld;
    private int expansions; 
    private boolean democracy;
     
    // Stored in (prefix)_citizens	
    //private String townMayor;
    protected Citizen mayor = new Citizen(plugin);
    protected TreeMap<String,Citizen> deputies   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> citizens   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> applicants = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected TreeMap<String,Citizen> invitees   = new TreeMap<String,Citizen>(String.CASE_INSENSITIVE_ORDER);
    protected HashMap<String,String> citizensMap = new HashMap<String,String>();
    
    private Timestamp createdDate;
    
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
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        townBankItemBal = copy.getBankItemBal();
        taxItemRate = copy.taxItemRate;
        taxRate = copy.getTaxRate();
        mayor = copy.mayor;
        this.democracy = copy.democracy;
        deputies = copy.deputies;
        citizens = copy.citizens;
        applicants = copy.applicants;
        invitees = copy.invitees;
        townWorld = copy.townWorld;
        expansions = copy.expansions;
    }
    
    /**
     * Constructor for starting a new town with specified player as the mayor
     * @param instance
     * @param town_Name
     * @param player 
     */
    public Town (Muni instance, String town_Name, String player , String world){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Town with mayor: "+town_Name+", "+player);
        townName = town_Name;
        mayor = new Citizen (plugin,townName,player,"mayor",null); 
        this.democracy = false;
        townRank = 1;
        townBankBal = 0;
        taxRate = 100;
        townBankItemBal = 0;
        taxItemRate = 16;
        townWorld = world;
        expansions = 0; 
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }    
    /**
     * Full data constructor
     * 
     * @author bobbshields
     */
    public Town (Muni instance, String town_Name, String mayor, String world, int expansions, boolean democracy,
            int rank, double bankBal, double tax, int itemBal, int itemTax){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Begin Muni Constructor: "+mayor+", "+town_Name);
        townName = town_Name;
        this.mayor = new Citizen (plugin,townName, mayor,"mayor",null);
        this.democracy = democracy;
        townRank = rank;
        townBankBal = bankBal;
        taxRate = tax;
        townBankItemBal = itemBal;
        taxItemRate = itemTax;
        townWorld = world;
        this.expansions = expansions; 
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }
    
    public boolean isValid(){
        boolean rtn = true;
        if (townName == null || !mayor.isValid() ){
            rtn = false; 
        }
        
        return rtn;
        
    }
    
    /**
     * Loads the town data from the database
     * @param town_Name
     * @return 
     */
    public boolean loadFromDB(String town_Name){
        Town copy = plugin.dbwrapper.getTown(town_Name);
        //plugin = copy.plugin;
        townName = copy.getName();
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        taxRate = copy.getTaxRate();
        townWorld = copy.getWorld();
        expansions = copy.getExpansions();
        
        
        if ( copy.getMayor() == null ) {return false;}
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
     * Gets the rankings value for this town to be compared against other towns of the same rank
     * @return Town's money bal + (item bal * rankup value per item)  
     */
    public double getRankingValue(){
        return townBankBal+ plugin.getRankupItemValueEach()*townBankItemBal;
    }
    
    /*
    public boolean renameTown (String newName ){
        boolean rtn = false;
        //copy then delete the old name in the index
        if (!plugin.isTown(newName) ){
            plugin.removeTown(townName);
            plugin.addTown(new Town(plugin,newName,));
        } //else there is already a town that name
        //add the new name in the index
        //check the existance of the town plot and change
        //check for existence of subregions and rename in db, then change in wg
        
        return rtn;
    }
    */
    
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
     * Returns a list of all current invitees
     * @return comma separated list of invitees
     */
    public String getAllInvitees(){
        String rtn = "";
        for (String curr : invitees.keySet() ){
            rtn = rtn + curr+", ";
        }
        if (rtn.length()>2){
            return rtn.substring(0, rtn.length()-2);
        }
        return "";
    }
    /**
     * Returns a list of all current applicants
     * @return comma separated list of invitees
     */
    public String getAllApplicants(){
        String rtn = "";
        for (String curr : applicants.keySet() ){
            rtn = rtn + curr+", ";
        }
        return rtn.substring(0, rtn.length()-2);
    }
    
    /**
     * Shows whether the applicants map is empty
     * @return 
     */
    public boolean hasApplicants(){
        return !applicants.isEmpty();
    }
    /**
     * Shows whether the applicants map is empty
     * @return 
     */
    public boolean hasInvitees(){
        return !invitees.isEmpty();
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
    
    public List<Citizen> getAllMembers(){
        List<Citizen> rtn = new ArrayList<Citizen>();
        
        if (mayor.isValid() ){
            rtn.add(mayor);
        }
        for (Citizen c: deputies.values() ) {
            if (c.isValid() ){
                rtn.add(c);
            }
        }
        for (Citizen c: citizens.values() ) {
            if (c.isValid() ){
                rtn.add(c);
            }
        }
        for (Citizen c: invitees.values() ) {
            if (c.isValid() ){
                rtn.add(c);
            }
        }
        for (Citizen c: applicants.values() ) {
            if (c.isValid() ){
                rtn.add(c);
            }
        }
        return rtn;
    }
    
    /**
     * Gives a string that has all the valid database column names
     * @return 
     */
    public String toDB_Cols(){
        return "townName,mayor,townRank,democracy,bankBal,taxRate,itemBal,itemTaxRate,world,expansions";
    }
    
    /**
     * Gives a string that has all the values for this town, follows order of toDB_Cols()
     * @return 
     */
    public String toDB_Vals(){
        return "'"+townName +"','"+mayor.getName()+"','"+
               Integer.toString(townRank) +"','"+Boolean.toString(democracy) +"','"+
               Double.toString(townBankBal) +"','"+Double.toString(taxRate)+"','"+
               Integer.toString(townBankItemBal) + "','"+Integer.toString(taxItemRate)+"','"+
               townWorld+"','"+expansions+"'";
    }  
    
    /**
     * Gives special string to allow a database update to the whole row in the wrapper
     * @return 
     */ 
    public String toDB_UpdateRowVals(){
        return "townName='"+townName+"', mayor='"+mayor.getName()+"', townRank='"+townRank
                +"', democracy='"+Boolean.toString(democracy)+"', bankBal='"+
                Double.toString(townBankBal)+"', taxRate='"+Double.toString(taxRate)+
                "', itemBal='"+ Integer.toString(townBankItemBal) +"', itemTaxRate='"+ Integer.toString(taxItemRate) +
                "', world='"+townWorld+"', expansions='"+expansions+"'";
    }
    
    /**
     * Queries the db to pull tax transaction history and displays to officer
     * @param officer
     * @param citizen
     * @return 
     */
    public boolean checkTaxes (Player officer, String citizen){
        
        if (!plugin.isCitizen(citizen) ){
            officer.sendMessage(citizen+" is not a member of any town");
            return false; 
        }
        
        if ( isOfficer(officer) ) {
            if (isCitizen(citizen) || isDeputy(citizen) || isMayor(citizen) ){
                officer.sendMessage("Checking tax history for: "+citizen);
            } else {
                officer.sendMessage(citizen+" is not a member of your town");
                return false;
            }
            List<Transaction> trans = plugin.dbwrapper.getTaxHistory(this, citizen);
            if (trans.isEmpty() ){
                officer.sendMessage(citizen+" has no tax history.");
            }
            for (Transaction tr : trans){
                officer.sendMessage(tr.toStringTaxesFormat() );
            }
            return true; 
        } else {
            officer.sendMessage("You're not an officer!") ; 
            return false; 
        }
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
        plugin.out( player, ChatColor.LIGHT_PURPLE+townName+" is a "+plugin.townRanks[townRank].getName() );
        plugin.out( player, "The bank balance is "+townBankBal+" with a tax rate of "+taxRate+".");
        plugin.out( player, "The town vault has "+townBankItemBal+" "+
                plugin.econwrapper.getRankupItemName() +" and the item tax rate is "+taxItemRate+".");
        listAllCitizens(player);
    }
    public List<String> getOfficerList(){
        List<String> rtn = new ArrayList<String>();
        rtn.add(mayor.getName());
        for (String name : deputies.keySet() ){
            rtn.add(name);
        }
        
        return rtn;
    }
    public List<String> getRegCitsList(){
        return getCitizenList(false);
    }
    public List<String> getAllCitsList () {
        return getCitizenList(true);
    }
    public List<String> getCitizenList(boolean officersToo){
        List<String> rtn ;
        if (officersToo){ 
            rtn = getOfficerList();
        } else { rtn = new ArrayList<String>(); }
        for (String name : citizens.keySet() ){
            rtn.add(name);
        }
        return rtn;
    }
    /**
     * Outputs to the player the lists of citizens involved with this town
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
                announce(player.getName()+" has left the town");
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
            if ( rankCostItem <= townBankItemBal ) {
                mayor.sendMessage("You have successfully ranked "+ townName +" to level " + (++townRank) );
                return paymentFromTB(rankCost, rankCostItem); 
            } else{ 
                mayor.sendMessage("You need to deposit "+plugin.econwrapper.getRankupItemName()+
                        " into the town bank to rank up "+ townName);
                return false;
            }
        }else {
            mayor.sendMessage("You need to depsoit "+ (rankCost-townBankBal) +" into the town bank to rank up "+ townName);
            return false;
        }  
    }
    public boolean paymentFromTB (double money, int item, String player, String reason){
        if (paymentFromTB(money,item) ){
            Transaction t = new Transaction(plugin, townName, player, reason, money, item, true);
            return true;
        }
        return false; 
    }
    
    /**
     * Withdraw and destroy from the town bank
     * @param amount
     * @return 
     */
    public boolean paymentFromTB (double money, int item){
        if ( townBankBal >= money && townBankItemBal >= item){
                townBankBal = townBankBal - money;
                townBankItemBal = townBankItemBal - item;
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
    
    public boolean tb_depositItems(Player officer, int amount){
        if (plugin.econwrapper.payItemR(officer, plugin.getRankupItemID(), amount, "bank") ){
            townBankItemBal = townBankItemBal + amount;
            messageOfficers(officer.getName()+" deposited "+amount+" " +
                    plugin.econwrapper.getItemName(plugin.getRankupItemID()) + " into the town bank");
            saveToDB();
            return true; 
        }
        return false;
    }
    public boolean tb_withdrawItems(Player officer, int amount) {
        if (townBankItemBal >= amount){
            plugin.econwrapper.giveItem(officer, plugin.getRankupItemID(), -amount);
            townBankItemBal = townBankItemBal - amount;
            messageOfficers(officer.getName()+" withdrew "+amount+" " +
                    plugin.econwrapper.getItemName(plugin.getRankupItemID()) + " from the town bank");
            
            Transaction t = new Transaction(plugin, townName, officer.getName(), "bank", 0, amount, true);
            saveToDB();
            return true; 
        }
        return false;
    }
    public void setItemTaxRate(int amount){
        taxItemRate = amount; 
    }
    
    /**
     * Player deposits funds into the town bank
     * @param player
     * @param amount
     * @return 
     */
    public boolean tb_deposit(Player player, double amount){
        if ( plugin.econwrapper.pay(player,amount,0,"bank") ){
            townBankBal = townBankBal + amount;
            messageOfficers(player.getName()+" deposited "+amount+" into the town bank");
            saveToDB();///// Change to having a new function that saves just the balance
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
            if (plugin.econwrapper.giveMoney(officer,-amount, "bank") ) {
                townBankBal = townBankBal - amount;
                messageOfficers(officer.getName()+" withdrew "+amount+" from the town bank");
                saveToDB(); ///// Change to having a new function that saves just the balance
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
    
    public void checkTownItemBank(CommandSender sender) {
            sender.sendMessage(townName+"'s town bank has a balance of "+ getBankItemBal()+" "+
                    plugin.econwrapper.getRankupItemName());
    }
    /**
     * Player pays the set amount of taxes
     * @param player
     * @return 
     */
    public boolean payTaxes(Player player){
        return payTaxes(player,taxRate,taxItemRate);
    }
    
    /**
     * Player pays a specified amount in taxes
     * @param player
     * @param amount
     * @return 
     */
    public boolean payTaxes(Player player, Double amount, int itemAmount){
        if ( plugin.econwrapper.pay(player, amount, itemAmount, "taxes" ) ){
            townBankBal = townBankBal + amount;
            player.sendMessage("You have paid taxes to "+townName+" of "+ amount+" "+
                    plugin.econwrapper.getCurrName(amount) +
            " and "+ itemAmount+" " + plugin.econwrapper.getRankupItemName()+".");
            messageOfficers(player.getName() +" has paid taxes:" +amount+" "
                    +plugin.econwrapper.getCurrName(amount) +
                " and "+ itemAmount+" " + plugin.econwrapper.getRankupItemName());
            return true;
        } else { return false; }
    }
    
    public void incrementExpansions(){
        expansions = expansions + 1;
    }
    
    /**
     * Returns the total number of expansions the town has experienced
     * @return 
     */
    public int getExpansions() { 
        return expansions; 
    }
    
    /**
     * Checks the town bank balance
     * @return 
     */
    public double getBankBal (){
        return townBankBal;
    }
    
    public int getBankItemBal() {
        return townBankItemBal;
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
        if (rate < plugin.maxTaxRate && rate > 0 ){
            taxRate = rate;
            return true;
        } else{
            return false;
        }
    }
    
    
    public Location getCenter (){
        return townCenter;
    }
    public boolean setCenter (Location center ){
        if (center != null) {
            townCenter = center;
            return true;
        } else {return false;}
      
    }
    public boolean setCenter (World world, int X, int Y, int Z ){
        if ( world != null) {
            townCenter = new Location(world, X, Y, Z);
            return true;
        } else {return false;}
      
    }
    
    public String getWorld(){
        return townWorld;
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
    
    /**
     * Custom hashCode 
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


