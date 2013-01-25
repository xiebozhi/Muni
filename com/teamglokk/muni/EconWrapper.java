/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.teamglokk.muni.EconWrapper;

//import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
/**
 *
 * @author Bobb
 */
public class EconWrapper extends Muni {
    private Muni plugin = null;
    private Economy econ = null;
    private EconomyResponse er = null;
    
    protected EconWrapper(Muni instance) {
        plugin = instance;
        econ = plugin.economy;
    }
    boolean pay( Player PLAYER, double amount){ 
        er = econ.withdrawPlayer(PLAYER.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }
    boolean pay_item( Player PLAYER, int ITEM_NUMBER, int amount){ 
        if (PLAYER.getInventory().contains(ITEM_NUMBER,amount) ){
            
            PLAYER.getInventory().removeItem(new ItemStack[] {
          new ItemStack(Material.getMaterial(ITEM_NUMBER),amount ) } );    
            return true;
        } else{
            return false;
        }
    }
    double getBalance(Player PLAYER){
        return econ.getBalance(PLAYER.getName() );
    }
            
    boolean giveMoney( Player PLAYER, double amount){ 
        er = econ.depositPlayer(PLAYER.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                return false;
            } 
    }
}
