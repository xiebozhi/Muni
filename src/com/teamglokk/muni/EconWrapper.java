package com.teamglokk.muni;

//import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Makes the Vault and Bukkit perm commands easier to work with
 * @author BobbShields
 */
public class EconWrapper extends Muni {
    private Muni plugin = null;
    private Economy econ = null;
    private EconomyResponse er = null;
    
    public EconWrapper(Muni instance) {
        plugin = instance;
        econ = plugin.economy;
    }
    public boolean checkBal (Player player, double amount) {
        
        return false;
    }
    public boolean checkItem (Player player, int ItemID, double amount) {
        
        return false;
    }
    public boolean pay( Player player, double amount){ 
        er = econ.withdrawPlayer(player.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }
    public boolean payR(Player player, double amount, String reason){
        if ( pay(player,amount) ){
            player.sendMessage("You paid "+amount+" "+econ.currencyNamePlural()+
                    " for "+reason);
            // make a new transaction here
            return true;
        } else { 
            
            return false;
        }
    }
    
    public boolean payItemR(Player player, int ItemID, int amount, String reason){
        if ( payItem(player,ItemID,amount) ){
            player.sendMessage("You paid "+amount+" of "+ItemID+
                    " for "+reason);
            // make a new transaction here
            return true;
        } else { 
            
            return false;
        }
    }
    public boolean payItem( Player player, int ItemID, int amount){ 
        if (player.getInventory().contains(ItemID,amount) ){
            
            player.getInventory().removeItem(new ItemStack[] {
          new ItemStack(Material.getMaterial(ItemID),amount ) } );   
            // Log a transaction here
            return true;
        } else{
            player.sendMessage("You did not have enough "+ItemID );
            return false;
        }
    }
    public double getBalance(Player player){
        return econ.getBalance(player.getName() );
    }
            
    public boolean giveMoney( Player player, double amount){ 
        er = econ.depositPlayer(player.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }
    public boolean hasPerm (Player player, String perm){
        if ( player.hasPermission(perm) ){
            return true;
        } else{
            return false;
        }
    }
    public String getCurrNameSingular(){
        return econ.currencyNameSingular();
    }
    public String getCurrNamePlural(){
        return econ.currencyNamePlural();
    }
}
