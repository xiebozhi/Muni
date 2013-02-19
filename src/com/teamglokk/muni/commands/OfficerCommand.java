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
package com.teamglokk.muni.commands;

import com.teamglokk.muni.Citizen;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.teamglokk.muni.Muni;
import com.teamglokk.muni.Town;
/**
 * Handler for the /town command.
 * @author BobbShields
 */
public class OfficerCommand implements CommandExecutor {
    private Muni plugin;
    private Player officer;
    
    public OfficerCommand (Muni instance){
            plugin = instance;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot send deputy or mayor commands from the console");
            return true;
        }
        officer = (Player) sender;
        

        if (split.length == 0){  //tested and working - 18 Feb 13
            displayHelp(sender, command.getName() );
            return true;
        } else if (split[0].equalsIgnoreCase("help")  ) { //tested and working - 18 Feb 13
            displayHelp(sender, command.getName() );
            return true;
        } else if (split[0].equalsIgnoreCase("found") || //not tested - 18 Feb 13
                split[0].equalsIgnoreCase("charter") ||split[0].equalsIgnoreCase("add")) {
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            if (plugin.econwrapper.pay(officer, plugin.townRanks[1].getMoneyCost(),
                    plugin.townRanks[1].getItemCost(), "Founding a town" ) ){
                Town t = new Town( plugin, split[1], officer.getName() );
                plugin.towns.put(t.getName(), t );
                plugin.allCitizens.put(officer.getName(), t.getName() );
                
                officer.sendMessage("You have founded "+t.getName());
            } else { officer.sendMessage("Could not found the town" ); }
            return true;
        } else if (split[0].equalsIgnoreCase("invite")) { //tested and working - 18 Feb 13
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.invite( split[1],officer);
            officer.sendMessage("Invitation to "+temp.getName()+" was sent.");
            return true;
            
        }  else if (split[0].equalsIgnoreCase("decline")) {  //not tested - 18 Feb 13
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.declineApplication( split[1],officer );
            return true;
            
        }  else if (split[0].equalsIgnoreCase("accept") ) {  //tested and working - 18 Feb 13
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.acceptApplication(split[1], officer);
            return true;
            
        } else if (split[0].equalsIgnoreCase("delete")  //tested and not working think its now fixed - 18 Feb 13
                || split[0].equalsIgnoreCase("disband")) {
            // This does not but should remove all players from citizens who are members of town
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            plugin.removeTown(temp.getName() );
            officer.sendMessage("Removed: "+ temp.getName() );
            return true;
        } else if (split[0].equalsIgnoreCase("checkTaxes")) {
            officer.sendMessage("Checking taxes will come in time.  Use the DB for now");
            return true;
        }  else if (split[0].equalsIgnoreCase("setTax")) { //tested and working - 18 Feb 13
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.setTaxRate(Double.parseDouble(split[1]) );
            officer.sendMessage("You have set the tax rate for "+temp.getName()+ " to "+ split[1] );
            return true;
        } else if (split[0].equalsIgnoreCase("kick")) {  //Assumed working (removed member, needs new output) - 18 Feb 13
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            officer.sendMessage("The player is a "+temp.getRole( split[1] ) );
            if ( temp.removeCitizen(split[1], officer ) ){
                officer.sendMessage("Player removed");
            } else { officer.sendMessage("Error"); }
            return true;
            
        } else if (split[0].equalsIgnoreCase("bank")) { //tested and working - 18 Feb 13
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            switch (split.length){
                case 1:
                    plugin.getTown(plugin.getTownName( officer.getName() ) ).checkTownBank(officer);
                    break;
                case 2:
                    temp.checkTownBank(officer);
                    break;
                case 3: 
                    if (split[1].equalsIgnoreCase("deposit") || split[1].equalsIgnoreCase("d") ){
                        double amount = Double.parseDouble( split[2] );
                        if (temp.tb_deposit(officer, amount ) ) {
                            plugin.out(officer,"You have deposited "+amount+" into your town's bank" );
                            plugin.out(officer,"Your personal balance is now: "+plugin.econwrapper.getBalance(officer) );
                            temp.checkTownBank(officer);
                        }
                        else {
                            plugin.out(officer,"You don't have enough to deposit");
                        } 
                        return true;
                    } else if (split[1].equalsIgnoreCase("withdraw") || split[1].equalsIgnoreCase("w") ){
                        double amount = Double.parseDouble( split[2] );
                        if (temp.tb_withdraw(officer, amount) ) {
                            plugin.out(officer,"You have withdrawn "+amount+" from your town's bank" );
                            plugin.out(officer,"Your personal balance is now: "+plugin.econwrapper.getBalance(officer) );
                            temp.checkTownBank(officer);
                        } else {
                            plugin.out( sender,"The town bank didn't have enough to withdraw" );
                        }
                    } else if (split[1].equalsIgnoreCase("check") || split[1].equalsIgnoreCase("c") ){
                        temp.checkTownBank(officer);
                    } else {
                        plugin.out(sender,"/town bank - ERROR (subcommand not recognized)");
                    }
                    break;
                default:
                        plugin.out(sender,"Invalid number of parameters");
                        return false; 
            }
            return true;
        } else if (split[0].equalsIgnoreCase("deputize")) { // buggy - 18 Feb 13
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) ); //throwing NPE
            temp.makeDeputy( split[1] ,officer);
            return true;
            
        } else if (split[0].equalsIgnoreCase("resign")) { //# of params was wrong, needs testing - 18 Feb 13
            if (split.length != 1) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            if ( temp.isMayor(officer) ) {
                temp.resignMayor(officer);
                return true;
            } else if ( temp.isDeputy(officer) ) {
                temp.resignDeputy(officer);
                return true;
            }
        } else if (split[0].equalsIgnoreCase("rankup")) { //working but needs better output - 18 Feb 
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.rankup(officer);
            return true;
            
        } else if (split[0].equalsIgnoreCase("setTax")) { //tested and working - 18 Feb
            if (split.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.setTaxRate(Double.parseDouble(split[1]) ) ;
            return true;
        } else {
            displayHelp( officer, split[0] );
            return true;
        }
        return false;
    }
    private void displayHelp(CommandSender sender, String subcmd){ // Tested and working - 18 feb
        if (subcmd.equalsIgnoreCase("deputy") ){
            plugin.out(sender, "Deputy command help.  You can do these commands:");
            plugin.out(sender, "/deputy invite <playerName>");
            plugin.out(sender, "/deputy accept <playerName");
            plugin.out(sender, "/deputy decline <playerName>");
            plugin.out(sender, "/deputy kick <playerName>");
            plugin.out(sender, "/deputy resign");
            //plugin.out(player, "/deputy regions");
            //plugin.out(player, "/deputy setRegion");
            //plugin.out(player, "/deputy checkTaxes");
            //plugin.out(player, "**/deputy pushBorder");
            plugin.out(sender, "**/deputy bank deposit");
            plugin.out(sender, "** (with perm) ");
        } else if (subcmd.equalsIgnoreCase("mayor") ){
            plugin.out(sender, "Mayors may also do all the deputy commands (/deputy help)");
            //plugin.out(player, "Mayor command help.  You can do all the above commands with /mayor and these:");
            plugin.out(sender, "/mayor bank");
            plugin.out(sender, "/mayor bank withdraw");
            plugin.out(sender, "/mayor deputize");
            plugin.out(sender, "/mayor resign");
            plugin.out(sender, "/mayor delete");
            plugin.out(sender, "/mayor rankup");
        }
    }
        
   
}
