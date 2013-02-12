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
import java.util.Date;
import org.apache.commons.lang.builder.HashCodeBuilder;

import org.bukkit.entity.Player;
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
    private Timestamp sentDate = null;
    
    //Not really needed yet but may be added in future
    private Timestamp lastLogin = null;
    
    public Citizen (Muni instance){
        plugin = instance;
    }
    public Citizen (Muni instance, Player player){
        plugin = instance; 
        loadFromDB ( player.getName() );
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
        this.invitedBy = cit.getInviteOfficer();
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
        this.invitedBy = cit.getInviteOfficer();
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
    public String info(){
        return toDB_Vals();
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
    
    public boolean isApplicant(){ return applied;}
    public boolean isInvited() {return invited;}
    public String getInviteOfficer() {return invitedBy;}
    public boolean isMayor() { return mayor;}
    public boolean makeMember () {
        applied = false;
        invited = false;
        return true;
    }
    public boolean isMember() {
        if (applied ||invited)
        { return false; }
        else { return true; }
    }
    //public getSentDate ();
    public boolean isOfficer(String town_Name){
        if (townName.equals(town_Name) ) {
            if (isMayor() || isDeputy() ){
                return true;
            }
        }
        return false;
    }
    public boolean isOfficer(){
        if (isMayor() || isDeputy() ){
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        return name;
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 13). 
            append(name).toHashCode(); //append(townName).
        // town Name append removed for temporary basis
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        } else if(obj.getClass() != getClass()){
            return false;
        } else if ( toString().equals(obj.toString() ) ) {
            return true;
        } else { return false;}
    }
    @Override
    public int compareTo(Citizen c){
        return this.getName().compareToIgnoreCase( c.getName() );
        /*
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
        * */
    }
    
}
