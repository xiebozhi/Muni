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
    public boolean payMoney( Player player, double amount){ 
        er = econ.withdrawPlayer(player.getName(), amount );
            if(er.transactionSuccess()) {
                return true;
            } else {
                player.sendMessage("You don't have enough money");
                return false;
            } 
    }
    public boolean payMoneyR(Player player, double amount, String reason){
        if ( payMoney(player,amount) ){
            player.sendMessage("You paid "+amount+" "+econ.currencyNamePlural()+
                    " for "+reason);
            // make a new transaction here
            Transaction t = new Transaction (plugin,plugin.getTown(player).getName(),player.getName(),reason,amount,0,true);
            return true;
        } else { 
            
            return false;
        }
    }
    public boolean pay(Player player, Double money, int items, String reason){
        // Double check to make sure the player is online
        if (plugin.getServer().getPlayer(player.getName()) != null ){
            // Check to make sure player has enough items to pay
            if (player.getInventory().contains(plugin.rankupItemID,items) ){
                // then pay money (checks to make sure they have enough)
                if (payMoney(player,money) ){
                    // then pay items and return the status
                    boolean rtn = payItem(player,plugin.rankupItemID,items);
                    if (rtn) {
                        Transaction t  = new Transaction (plugin,plugin.getTown(player).getName(),
                                player.getName(),reason,money,items,true);
                        player.sendMessage( t.toString() );
                    }
                    return rtn;
                } else {return false;} // not enough money
            } else {return false;}  // didn't have enough items to test for money
        } else {return false;} // not online
    }
    
    public boolean payItemR(Player player, int ItemID, int amount, String reason){
        if ( payItem(player,ItemID,amount) ){
            player.sendMessage("You paid "+amount+" of "+ItemID+
                    " for "+reason);
            // make a new transaction here
            Transaction t = new Transaction (plugin,plugin.getTown(player).getName(),player.getName(),reason,0,amount,true);
            return true;
        } else { 
            
            return false;
        }
    }
    public boolean payItem( Player player, int ItemID, int amount){ 
        if (player.getInventory().contains(ItemID,amount) ){
            player.sendMessage("Taking "+amount+" items");
            
            player.getInventory().removeItem( new ItemStack[] {
                new ItemStack( Material.getMaterial(ItemID),amount ) } );
            
            return true;
        } else{
            player.sendMessage("You did not have enough "+getItemName( ItemID ) );
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
    public boolean giveItem( Player player, int ItemID, int amount){ 
        player.getInventory().addItem( new ItemStack[] {
                new ItemStack( Material.getMaterial(ItemID),amount ) } );
        return true;
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
    public String getCurrName(Integer i){
        return getCurrName( Double.parseDouble( i.toString() ) );
    }
    public String getCurrName (double i){
        if (i > 1){
            return getCurrNamePlural();
        } else { return getCurrNameSingular();}
    }
    public String getItemName(int itemNumber){
        return Material.getMaterial(plugin.rankupItemID).toString();
    }
}
