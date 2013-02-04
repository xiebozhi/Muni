package com.teamglokk.muni;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Citizen implements Comparable<Citizen> {
    
    private static Muni plugin;
    
    // Database table is $db_prefix_ citizens
    //private int db_pk;
    private String name =null;
    private String townName =null;
    
    // Stored in (prefix)_citizens
    private boolean mayor =false;
    private boolean deputy = false;
    
    
    private boolean applied = false;  
    private boolean invited = false;
    private String invitedBy = null;
    private Date sentDate = null;
    
    //Not really needed yet but may be added in future
    private String lastLogin = null;
    
    public Citizen (Muni instance){
        plugin = instance;
        
    }
    public Citizen (Muni instance, String player){
        plugin = instance; 
        loadFromDB(player);
    }
    public Citizen (Muni instance, String town_Name, String player ){
        plugin = instance;
        addCitizen (town_Name,player);
    }
    public Citizen (Muni instance, String town_Name, String player, boolean mayor,
            boolean deputy, boolean applied, boolean invited, String invitedBy) { //, Date sentDate){
        plugin = instance;
        addCitizen (town_Name,player);
        this.mayor = mayor;
        this.deputy = deputy;
        this.applied = applied;
        this.invited = invited;
        this.invitedBy = invitedBy;
        this.sentDate = sentDate;
    }    
    public Citizen (Citizen cit){
        //this.plugin = cit.plugin;
        this.name = cit.getName();
        this.townName = cit.getTown();
        this.mayor = cit.isMayor();
        this.deputy = cit.isDeputy();
        this.applied = cit.isApplicant();
        this.invited = cit.isInvited();
        this.invitedBy = cit.isInviteOfficer();
        //this.sentDate = cit;
    }
    public boolean loadFromDB(String player){
        Citizen cit = plugin.dbwrapper.getCitizen(player);
        
        //this.plugin = cit.plugin;
        this.name = cit.getName();
        this.townName = cit.getTown();
        this.deputy = cit.isDeputy();
        this.applied = cit.isApplicant();
        this.invited = cit.isInvited();
        this.invitedBy = cit.isInviteOfficer();
        //this.sentDate = cit;
        
        return true;
    }    
    public boolean saveToDB(){
        // if exists, update; else insert
        //db_updateRow(String table, String key_col, String key, String colsANDvals
        if ( plugin.dbwrapper.checkExistence("citizens", "playerName", name) ){
            return plugin.dbwrapper.updateRow("citizens", "playerName", name, toDB_UpdateRowVals());
        } else {
            return plugin.dbwrapper.insert("citizens", toDB_Cols(), toDB_Vals() );
        }
    }
    public String toDB_UpdateRowVals(){
        return "playerName='"+name+"', townName='"+townName+"', mayor='"+
                mayor +"', deputy='"+deputy+"', applicant='"+applied+
                "', invitee='"+invited+"', invitedBy='"+invitedBy+"' ";
        // this is missing the sentDate and lastLogin SQL fields
    }
    public String toDB_Cols(){
        return "playerName,townName,mayor,deputy,applicant,invitee,invitedBy"; //,sentDate,lastLogin";
    }
    public String toDB_Vals(){
        return "'"+name+"', '"+townName+"', '"+mayor+"', '"+deputy+"', '"+applied+"', '"+
                invited+"', '"+invitedBy+"'"; //+", "+sentDate+", "+lastLogin;
    }
    public boolean isApplicant(){ return applied;}
    public boolean isInvited() {return invited;}
    public String isInviteOfficer() {return invitedBy;}
    public boolean isMayor() { return mayor;}
    
    public boolean makeMember () {
        applied = false;
        invited = false;
        return true;
    }
    //public getSentDate ();
    
    public void addCitizen(String town, String player ) {
        name = player;
        townName = town;
    } 
    public void inviteCitizen (String town, String player, String officer ){
        addCitizen(town,player);
        invited = true;
        invitedBy = officer;
        //sentDate = Calendar.getInstance();
    }
    public void apply4Citizenship(String town, String player){
        addCitizen(town,player);
        applied = true;
        //sentDate = Calendar.getInstance();
    }
    public boolean isMayor(String town_Name){
        if (townName.equals(town_Name) ){
            return mayor;
        } else {
            plugin.getLogger().warning(name+" is not a member of "+town_Name+" so they cannot be mayor"); 
            return false;
        }
    }
    public boolean setMayor(String town_Name, boolean value){
        if (townName.equals(town_Name) ){
            mayor = value;
            return true;
        } else {
            plugin.getLogger().warning(name+" is not a member of "+town_Name+" so they cannot become mayor"); 
            return false;
        }
    }
    public boolean isDeputy(){return deputy; }
    public boolean isDeputy(String town_Name){
        if (townName.equals(town_Name) ){
            return deputy;
        } else {
            plugin.getLogger().warning(name+" is not a member of "+town_Name+" so they cannot be a deputy"); 
            return false;
        }
    }
    public boolean setDeputy(String town_Name, boolean value){
        if (townName.equals(town_Name) ){
            deputy = value;
            return true;
        } else {
            plugin.getLogger().warning(name+" is not a member of "+town_Name+" so they cannot become a deputy"); 
            return false;
        }
    }
    public boolean checkOfficer (String town_Name) {
        if (townName.equals(town_Name) ){
            if (mayor) {return true;}
            if (deputy) {return true;}
            return false; 
        } else {
            plugin.getLogger().warning(name+" is not a member of "+town_Name+" so they cannot be an officer"); 
            return false;
        }
    }

    public String getName(){
        return name;
    }
    public boolean setName(String name){
        this.name = name;
        return true;
    }
    public String getTown(){
        return townName;
    }
    public boolean setTown(String name){
        townName = name;
        return true;
    }
         
    @Override
    public String toString(){
        return name;
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 13). 
            append(name).append(townName).toHashCode();
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
    public int compareTo(Citizen c){
        //Mayors are bigger than deputies,
        // Failover if everything is equal: return based on the name
        if (this.isMayor() && !c.isMayor() ){
            return 1;
        } else if (c.isMayor() && !this.isMayor() ){
            return -1;
        }
        if (this.isDeputy() && !c.isMayor() && !c.isDeputy() ) {
            return 1;
        } else if (c.isDeputy() && !this.isMayor() && !this.isDeputy() ){
            return -1;
        } else { 
            return this.getName().toLowerCase().compareTo( c.getName().toLowerCase() );
        }
    }
    
}
