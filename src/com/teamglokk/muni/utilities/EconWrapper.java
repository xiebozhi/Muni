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

//import net.milkbowl.vault.chat.Chat;
import com.teamglokk.muni.Muni;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

/**
 * Makes the Vault and Bukkit perm commands easier to work with
 * @author BobbShields
 */
public class EconWrapper extends Muni {
    private Muni plugin = null;
    private Economy econ = null;
    private EconomyResponse er = null;
    
    /**
     * Default constructor
     * @param instance 
     */
    public EconWrapper(Muni instance) {
        plugin = instance;
        econ = plugin.economy;
    }
    
    /**
     * Checks to see if player has enough to cover the amount
     * @param player
     * @param amount
     * @return 
     */
    public boolean checkBal (Player player, double amount) {
        
        return false;
    }
    
    /**
     * Checks to see if the player has enough items to cover the amount
     * @param player
     * @param ItemID
     * @param amount
     * @return 
     */
    public boolean checkItem (Player player, int ItemID, double amount) {
        
        return false;
    }
    
    /**
     * The player pays the amount of money
     * @param player
     * @param amount
     * @return 
     */
    public boolean payMoney( Player player, double amount){ 
        er = econ.withdrawPlayer(player.getName(), amount );
        return (er.transactionSuccess());
    }
    
    /**
     * The player pays the amount of money and it logs a transaction with a reason
     * @param player
     * @param amount
     * @param reason
     * @return 
     */
    public boolean payMoneyR(Player player, double amount, String reason){
        if ( payMoney(player,amount) ){
            player.sendMessage("You paid "+amount+" "+econ.currencyNamePlural()+" for "+reason);
            Transaction t = new Transaction (plugin,plugin.allCitizens.get( player.getName() ),player.getName(),reason,amount,0,true);
            return true;
        } else { 
            
            return false;
        }
    }
    
    /**
     * The player pays money and so many of the rankup material, and it logs a reason
     * @param player
     * @param money
     * @param items
     * @param reason
     * @return 
     */
    public boolean pay(Player player, Double money, int items, String reason){
        // Double check to make sure the player is online
        if ( player.isOnline() ){
            // Check to make sure player has enough items to pay
            if (player.getInventory().contains(plugin.getRankupItemID(),items) ){
                // then pay money (checks to make sure they have enough)
                if (payMoney(player,money) ){
                    // then pay items and messages the user
                    payItem(player,plugin.getRankupItemID(),items);
                    String itemString = "";
                    if (items>=0) {
                        itemString = " and "+items+" "+getItemName(plugin.getRankupItemID() ) ;
                    }
                    player.sendMessage("Took " +money+" "+ getCurrName(money)+ itemString+ " as payment for " + reason);
                    Transaction t  = new Transaction (plugin,plugin.allCitizens.get( player.getName() ),
                            player.getName(),reason,money,items,true);
                    return true;
                } else {
                    double slack = money-getBalance(player);
                    player.sendMessage("You need "+ slack +" more "+getCurrName(slack)+" to complete the transaction");
                    return false;
                } 
            } else {
                double slack = items - checkRankupItemAmount(player);
                player.sendMessage("You need "+ slack +" more " + getItemName(plugin.getRankupItemID())+" to complete the transaction");
                return false;
            }  
        } else {return false;} // not online
    }
    
    /**
     * Returns the number of rankup items the player has in their inventory
     * @param player
     * @return 
     */
    public int checkRankupItemAmount (Player player) {
        int rtn = 0;
        Map<Integer, ? extends ItemStack> mapping = player.getInventory().all( plugin.getRankupItemID() );
        for (ItemStack i : mapping.values() ){
            rtn = rtn + i.getAmount();
        }
        return rtn; 
    }
    
    /**
     * The player pays a certain number of items and it logs a reason
     * @param player
     * @param ItemID
     * @param amount
     * @param reason
     * @return 
     */
    public boolean payItemR(Player player, int ItemID, int amount, String reason){
        if ( payItem(player,ItemID,amount) ){
            player.sendMessage("You paid "+amount+" of "+ItemID+
                    " for "+reason);
            // make a new transaction here
            Transaction t = new Transaction (plugin,plugin.allCitizens.get( player.getName() ),player.getName(),reason,0,amount,true);
            return true;
        } else { 
            
            return false;
        }
    }
    
    /**
     * The player pays a certain number of items
     * @param player
     * @param ItemID
     * @param amount
     * @return 
     */
    public boolean payItem( Player player, int ItemID, int amount){ 
        if (player.getInventory().contains(ItemID,amount) ){
           // player.sendMessage("Taking "+amount+" items");
            
            player.getInventory().removeItem( new ItemStack[] {
                new ItemStack( Material.getMaterial(ItemID),amount ) } );
            
            return true;
        } else{
            //player.sendMessage("You did not have enough "+getItemName( ItemID ) );
            return false;
        }
    }
    
    /**
     * Gets the player's current amount of money
     * @param player
     * @return 
     */
    public double getBalance(Player player){
        return econ.getBalance(player.getName() );
    }
            
    /**
     * Gives the player money
     * @param player
     * @param amount
     * @return 
     */
    public boolean giveMoney( Player player, double amount){ 
        er = econ.depositPlayer(player.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }             
    /**
     * Gives the player money
     * @param player
     * @param amount
     * @return 
     */
    public boolean giveMoney( Player player, double amount,String reason){ 
        er = econ.depositPlayer(player.getName(), amount );
            if(er.transactionSuccess()) {
                Transaction t = new Transaction (plugin,plugin.allCitizens.get( player.getName() ),player.getName(),reason,-amount,0,true);
                player.sendMessage( "Transaction logged." );
                return true;
            } else {
                return false;
            } 
    }     
    
    /**
     * Gives the player items
     * @param player
     * @param ItemID
     * @param amount
     * @return 
     */
    public boolean giveItem( Player player, int ItemID, int amount){ 
        player.getInventory().addItem( new ItemStack[] {
                new ItemStack( Material.getMaterial(ItemID),amount ) } );
        return true;
    }
    
    /**
     * Checks the player's permission
     * @param player
     * @param perm
     * @return 
     */
    public boolean hasPerm (Player player, String perm){
        if ( player.hasPermission(perm) ){
            return true;
        } else if (plugin.useOP() && player.isOp() ) {
            return true;
        }
        else { return false; }
    }
    
    /**
     * Gets the singular currency name
     * @return 
     */
    public String getCurrNameSingular(){
        return econ.currencyNameSingular();
    }
    /**
     * Gets the plural currency name
     * @return 
     */
    public String getCurrNamePlural(){
        return econ.currencyNamePlural();
    }
    
    /**
     * Decides on singular or plural currency name based on the parameter
     * @param i
     * @return 
     */
    public String getCurrName(Integer i){
        return getCurrName( Double.parseDouble( i.toString() ) );
    }
    
    /**
     * Decides on singular or plural currency name based on the parameter
     * @param i
     * @return 
     */
    public String getCurrName (double i){
        if (i > 1){
            return getCurrNamePlural();
        } else { return getCurrNameSingular();}
    }
    
    /**
     * Gets the material name based on the parameter
     * @param itemNumber
     * @return 
     */
    public String getItemName(int itemNumber){
        String item = Material.getMaterial(plugin.getRankupItemID()).toString()
                .toLowerCase().trim();
        String s = (itemNumber>1) ? "s":"" ;
        return item+s;
    }
    public String getRankupItemName(){
        return getItemName(plugin.rankupItemID ) ;
    }
}
