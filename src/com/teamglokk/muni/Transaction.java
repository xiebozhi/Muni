package com.teamglokk.muni;

//import java.util.Calendar;
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
                item_amount+" "+Material.getMaterial(plugin.rankupItemID)+" on "+datetime+".";
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
