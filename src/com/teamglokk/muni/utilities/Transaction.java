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
package com.teamglokk.muni.utilities;

//import java.util.Calendar;
import com.teamglokk.muni.Muni;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Material;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Transaction {
    
    private static Muni plugin;
    
    // Database table is $db_prefix_ transactions
    //private int db_pk;
    private String playerName =null;
    private String townName =null;
    private Timestamp datetime = null;
    //private Calendar time = null;
    private String type = null;
    private double amount = 0;
    private int item_amount = 0;
    private String notes = null;
    
    public Transaction (Muni instance){
        plugin = instance;
        
    }
    public Transaction (Muni instance, String town_Name, String player, 
            String reason, double payment, int item_payment ){
        plugin = instance;
        playerName = player;
        townName = town_Name;
        type = reason;
        amount = payment;
        item_amount = item_payment;
        datetime = new Timestamp ( System.currentTimeMillis() );
                //= Calendar.getInstance();
        //time = Calendar.getInstance();
    }
    public Transaction (Muni instance, String town_Name, String player, 
            String reason, double payment, int item_payment, boolean autosave ){
        plugin = instance;
        playerName = player;
        townName = town_Name;
        type = reason;
        amount = payment;
        item_amount = item_payment;
        datetime = new Timestamp ( System.currentTimeMillis() );
                // = Calendar.getInstance();
        //time = Calendar.getInstance();
        if (autosave){ saveTrans(); }
    }
    
    private String db_Cols(){
        return "playerName,townName,timestamp,type,amount,item_amount,notes";
    }
    
    private String db_Vals(){
        return "'"+playerName+"', '"+townName+"', '"+datetime+"', '"+type
                +"', '"+amount+"', '"+item_amount+"', '"+notes+"'";
    }
    public boolean saveTrans(){
        if (plugin.dbwrapper.insert("transactions", db_Cols(), db_Vals() )){
            return true;
        } else {
            plugin.getLogger().severe("Could not insert the transaction into the database:");
            plugin.getLogger().severe( toString() );
            return false;
        }
    }
    
    @Override
    public String toString(){
        return "Player "+playerName+" for town "+townName+" made a payment for "+type+" of "+
                amount+" "+plugin.econwrapper.getCurrName(amount)+" and "+
                item_amount+" "+Material.getMaterial(plugin.getRankupItemID())+" on "+datetime+".";
                //+date()+" at "+time()+".";
    }
    /*
    public Calendar date(){
        Calendar temp = null;
        
        return temp;
    }
    public Calendar time() {
        Calendar  
    }
    */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 13). 
            append(playerName).append(townName).append(amount).
                append(item_amount).append(datetime).toHashCode();
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
