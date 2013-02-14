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
 * Citizen.java: defines the Citizen class
 * @author bobbshields
 */
public class Citizen implements Comparable<Citizen> {
    
    private static Muni plugin;
    
    // Database table is $db_prefix_ citizens
    //private int db_pk;
    private String name =null;
    private String townName =null;
    private String role = null;
    private String invitedBy = null;
    private Timestamp sentDate = null;
    
    //Not really needed yet but may be added in future
    private Timestamp lastLogin = null;
    
    /**
     * The different types of citizen, such as mayor, deputy, etc.
     */
    public enum roles {
        MAYOR("mayor"),
        DEPUTY("deputy"),
        CITIZEN("citizen"),
        INVITEE("invited"),
        APPLICANT("applied");
        String value;
        roles (String s) {value = s; }
        public String getValue() {return value;}
        public static roles fromString(String text) throws IllegalArgumentException {
            if (text != null){
                for (roles r: roles.values() ){
                    if (text.equalsIgnoreCase(r.value) ){
                        return r;
                    }
                }
                throw new IllegalArgumentException("Type not found"); 
            }
            return null;
        }
    }
    
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
    public Citizen (Muni instance, String town_Name, String player, String role, String invitedBy) { //, Date sentDate){
        plugin = instance;
        this.townName = town_Name;
        this.name = player;
        this.role = role;
        this.invitedBy = invitedBy;
        this.sentDate = sentDate;
    }    
    public Citizen (Citizen cit){
        //this.plugin = cit.plugin;
        this.name = cit.getName();
        this.townName = cit.getTown();
        this.role = cit.role;
        this.invitedBy = cit.getInviteOfficer();
        //this.sentDate = cit;
    }
    public Citizen loadFromDB(String player){
        Citizen cit = plugin.dbwrapper.getCitizen(player);
        
        //this.plugin = cit.plugin;
        this.name = cit.getName();
        this.townName = cit.getTown();
        this.role = cit.getRole();
        this.invitedBy = cit.getInviteOfficer();
        //this.sentDate = cit;
        
        return new Citizen (this );
    }    
    public boolean loadFromTown(Town t, Player player) {
        
        this.name = player.getName();
        this.townName = t.getName();
        this.role = t.getRole( player.getName() );
        //this.invitedBy = t.getInviteOfficer(player);
        
        return true;
    }
    public boolean saveToDB(){
        // if exists, update; else insert
        //db_updateRow(String table, String key_col, String key, String colsANDvals
        if ( plugin.dbwrapper.checkExistence("citizens", "playerName", name) ){
            if (plugin.isDebug()) { plugin.getLogger().info(toDB_UpdateRowVals());}
            return plugin.dbwrapper.updateRow("citizens", "playerName", name, toDB_UpdateRowVals() );
        } else {
            if (plugin.isDebug()) { plugin.getLogger().info(toDB_Vals());}
            if (plugin.dbwrapper.insert("citizens", toDB_Cols(), toDB_Vals() ) ) {
                plugin.allCitizens.put(name, townName);
                return true;
            } else {return false; }
        }
    }
    public Citizen parseInvolvedCitizen(Town t, Player player) {
        Citizen rtn = new Citizen(plugin) ;
        
        rtn.name = player.getName();
        rtn.townName = t.getName();
        rtn.role = t.getRole(player.getName() );
        //this.invitedBy = t.getInviteOfficer(player);
        
        return rtn;
    }
    public String toDB_UpdateRowVals(){
        return "playerName='"+name+"', townName='"+townName+"', role='"+
                role +"', invitedBy='"+invitedBy+"' ";
        // this is missing the sentDate and lastLogin SQL fields
    }
    public String toDB_Cols(){
        return "playerName,townName,role,invitedBy"; //,sentDate,lastLogin";
    }
    public String toDB_Vals(){
        return "'"+name+"', '"+townName+"', '"+role+"', '"+invitedBy+"'"; //+", "+sentDate+", "+lastLogin;
    }
    public String info(){
        return toDB_Vals();
    }
    public void addCitizen(String town, String player ) {
        name = player;
        townName = town;
    } 
    
    public String getRoleFromEnum(String role ){
        String rtn = "";
        try {
            switch(roles.fromString(role) ){
                case MAYOR:
                    rtn =  "mayor";
                    break; 
                case DEPUTY:
                    rtn =  "deputy";
                    break; 
                case CITIZEN:
                    rtn =  "citizen";
                    break; 
                case INVITEE:
                    rtn =  "invitee";
                    break; 
                case APPLICANT:
                    rtn =  "applicant";
                    break; 
            }
        } catch (IllegalArgumentException ex){
            rtn = "invalid";
        } finally { return rtn; }
    }
    public boolean setRole (String role ) {
        String s = getRoleFromEnum(role);
        if (s.equals("invalid") ) {
            return false;
        } else { 
            role = s;
            return true; 
        }
    }
    public String getRole() { return role; }
    public void setMayor(){ role = "mayor"; }
    public void setDeputy() { role = "deputy"; }
    public void setCitizen() { role = "citizen"; }
    public void setApplicant() { role = "applicant"; }
    public void setInvitee() { role = "invitee"; }
    public void setInfvitee(String officer) { invitedBy = officer; setInvitee(); }
    public boolean isMayor(){
        return role.equalsIgnoreCase("mayor");
    }
    public boolean isDeputy(){
        return role.equalsIgnoreCase("deputy");
    }
    public boolean isCitizen(){
        return role.equalsIgnoreCase("citizen");
    }
    public boolean isApplicant(){
        return role.equalsIgnoreCase("applicant");
    }
    public boolean isInvitee(){
        return role.equalsIgnoreCase("invitee");
    }
    public String getInvitationOfficer(){
        return invitedBy;
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
    
    public String getInviteOfficer() {return invitedBy;}
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
