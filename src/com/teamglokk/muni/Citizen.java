package com.teamglokk.muni;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Citizen {
    
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
            boolean deputy, boolean applied, boolean invited, String invitedBy, Date sentDate){
        plugin = instance;
        addCitizen (town_Name,player);
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
        this.deputy = cit.getDeputy();
        this.applied = cit.getApplicant();
        this.invited = cit.getInvitee();
        this.invitedBy = cit.getInviteOfficer();
        //this.sentDate = cit;
    }
    public boolean loadFromDB(String player){
        Citizen cit = plugin.dbwrapper.getCitizen(player);
        
        //this.plugin = cit.plugin;
        this.name = cit.getName();
        this.townName = cit.getTown();
        this.deputy = cit.getDeputy();
        this.applied = cit.getApplicant();
        this.invited = cit.getInvitee();
        this.invitedBy = cit.getInviteOfficer();
        //this.sentDate = cit;
        
        return true;
    }
    public boolean getApplicant(){ return applied;}
    public boolean getInvitee() {return invited;}
    public String getInviteOfficer() {return invitedBy;}
    //public getSentDate ();
    @Override
    public String toString(){
        return name;
    }
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
    public String toDB_Cols(){
        return "playerName,townName,mayor,deputy,applicant,invitee,sentDate,lastLogin";
    }
    public String toDB_Vals(){
        return name+", "+townName+", "+mayor+", "+deputy+", "+applied+", "+
                invited+", "+sentDate+", "+lastLogin;
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
    public boolean getDeputy(){return deputy; }
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
}
