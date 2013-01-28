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
    public boolean pay( Player PLAYER, double amount){ 
        er = econ.withdrawPlayer(PLAYER.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }
    public boolean pay_item( Player PLAYER, int ITEM_NUMBER, int amount){ 
        if (PLAYER.getInventory().contains(ITEM_NUMBER,amount) ){
            
            PLAYER.getInventory().removeItem(new ItemStack[] {
          new ItemStack(Material.getMaterial(ITEM_NUMBER),amount ) } );    
            return true;
        } else{
            return false;
        }
    }
    public double getBalance(Player PLAYER){
        return econ.getBalance(PLAYER.getName() );
    }
            
    public boolean giveMoney( Player PLAYER, double amount){ 
        er = econ.depositPlayer(PLAYER.getName(), amount );
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
