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
import org.bukkit.ChatColor;
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
        String [] args = plugin.trimSplit(split);
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot send deputy or mayor commands from the console");
            return true;
        }
        officer = (Player) sender;
        if (!plugin.econwrapper.hasPerm(officer, "muni.deputy") ){
            officer.sendMessage("You do not have permission to run /deputy subcommands");
            return true; 
        }

        if (args.length == 0){  //tested and working - 18 Feb 13
            displayHelp(sender, command.getName() );
            return true;
        } else if (args[0].equalsIgnoreCase("help")  ) { //tested and working - 18 Feb 13
            displayHelp(sender, command.getName() );
            return true;
        } else if (args[0].equalsIgnoreCase("invite")) { //tested and working - 18 Feb 13
            if (args.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            if (temp.invite( args[1],officer) ){
                temp.messageOfficers("An invitation to "+args[1]+" was sent by "+officer.getName() );
                if (plugin.isOnline(args[1]) ) {
                    plugin.getServer().getPlayer(args[1]).sendMessage("You have been invited to "
                            +temp.getName()+". Do /town accept OR /town leave"); 
                }
            }
            return true;
            
        }  else if (args[0].equalsIgnoreCase("decline")) {  //not tested - 18 Feb 13
            if (args.length != 2) {
                officer.sendMessage("/deputy decline <applicant>");
                return true;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.declineApplication( args[1],officer );
            return true;
            
        }  else if (args[0].equalsIgnoreCase("accept") ) {  //tested and working - 18 Feb 13
            if (args.length != 2) {
                officer.sendMessage("/deputy accept <applicant>");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.acceptApplication(args[1], officer);
            return true;
            
        } else if (args[0].equalsIgnoreCase("checkTaxes")) {
            Town temp = plugin.getTownFromCitizen(officer.getName() );
            temp.checkTaxes(officer, args[1] );
            
            return true;
        }  else if (args[0].equalsIgnoreCase("setTax")) { 
            if (!plugin.econwrapper.hasPerm(officer, "muni.deputy.changetax") ||
                    !plugin.econwrapper.hasPerm(officer, "muni.mayor") ) {
            officer.sendMessage("You do not have permission to set the taxs");
            return true; 
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            try {
                if (temp.isOfficer(officer ) ){
                    temp.setTaxRate( plugin.parseD( args[1] ) );
                    temp.announce(officer.getName()+" has set the tax rate for "+temp.getName()+ " to "+ args[1] );
                } else{ officer.sendMessage("You are not an officer of "+temp.getName() ); }
                return true;
            } catch (Exception ex) {
                officer.sendMessage("You should write an actual number next time");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("setItemTax")) {
            if (!plugin.econwrapper.hasPerm(officer, "muni.deputy.changetax") ||
                    !plugin.econwrapper.hasPerm(officer, "muni.mayor") ) {
            officer.sendMessage("You do not have permission to set the taxs");
            return true; 
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            try {
                if (temp.isOfficer(officer ) ){
                    temp.setItemTaxRate( plugin.parseI( args[1] ) );
                    temp.announce(officer.getName()+" has set the item tax rate for "+temp.getName()+ " to "+ args[1] );
                } else{ officer.sendMessage("You are not an officer of "+temp.getName() ); }
                return true;
            } catch (Exception ex) {
                officer.sendMessage("You should write an actual number next time");
                return true;
            }
        } else if (args[0].equalsIgnoreCase("kick")) {  //Worked on bugs - 19 Feb 13
            if (args.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            if ( temp.removeCitizen(args[1], officer ) ){
            }
            return true;
            
        } else if (args[0].equalsIgnoreCase("resign")) { 
            if (args.length != 1) {
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
        } else if (args[0].equalsIgnoreCase("announce")) { 
            if (args.length == 1) {
                officer.sendMessage("/deputy announce <YOUR MSG HERE>");
                return false;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            if ( temp.isOfficer(officer) ) {
                String msg = ChatColor.DARK_AQUA+"["+temp.getName()+"] "+ChatColor.YELLOW;
                for (int i =1; i<args.length; i++ ){
                    msg = msg + args[i] +" ";
                }
                temp.announce(msg);
                return true;
            } 
        } else if (args[0].equalsIgnoreCase("bank")) { 
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            switch (args.length){
                case 1:
                    if (plugin.isCitizen(officer) ) {
                        plugin.getTown(plugin.getTownName( officer.getName() ) ).checkTownBank(officer); 
                    } else {officer.sendMessage("You are not part of a town"); }
                    break;
                case 2:
                    temp.checkTownBank(officer);
                    break;
                case 3: 
                    if (args[1].equalsIgnoreCase("deposit") || args[1].equalsIgnoreCase("d") ){
                        double amount = Double.parseDouble( args[2] );
                        if (temp.tb_deposit(officer, amount ) ) {
                            plugin.out(officer,"You have deposited "+amount+" into your town's bank" );
                            plugin.out(officer,"Your personal balance is now: "+plugin.econwrapper.getBalance(officer) );
                            temp.checkTownBank(officer);
                        }
                        else {
                            plugin.out(officer,"You don't have enough to deposit");
                        } 
                        return true;
                    } else if (args[1].equalsIgnoreCase("withdraw") || args[1].equalsIgnoreCase("w") ){
                        if ( !plugin.econwrapper.hasPerm(officer, "muni.deputy.changetax") ) {
                            officer.sendMessage("You do not have permission to withdraw from the town bank");
                            return true;
                        }
                        double amount = Double.parseDouble( args[2] );
                        if (temp.tb_withdraw(officer, amount) ) {
                            plugin.out(officer,"You have withdrawn "+amount+" from your town's bank" );
                            plugin.out(officer,"Your personal balance is now: "+plugin.econwrapper.getBalance(officer) );
                            temp.checkTownBank(officer);
                        } else {
                            plugin.out( sender,"The town bank didn't have enough to withdraw" );
                        }
                    } else if (args[1].equalsIgnoreCase("check") || args[1].equalsIgnoreCase("c") ){
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
        }else if (args[0].equalsIgnoreCase("itembank")) { 
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            switch (args.length){
                case 1:
                    plugin.getTown(plugin.getTownName( officer.getName() ) ).checkTownItemBank(officer);
                    break;
                case 2:
                    temp.checkTownBank(officer);
                    break;
                case 3: 
                    if (args[1].equalsIgnoreCase("deposit") || args[1].equalsIgnoreCase("d") ){
                        int amount = Integer.parseInt( args[2] );
                        if (temp.tb_depositItems(officer, amount ) ) {
                            plugin.out(officer,"You have deposited "+amount+" into your town's bank" );
                            temp.checkTownItemBank(officer);
                        }
                        else {
                            plugin.out(officer,"You don't have enough to deposit");
                        } 
                        return true;
                    } else if (args[1].equalsIgnoreCase("withdraw") || args[1].equalsIgnoreCase("w") ){
                        if ( !plugin.econwrapper.hasPerm(officer, "muni.deputy.changetax") ) {
                            officer.sendMessage("You do not have permission to withdraw from the town bank");
                            return true;
                        }
                        int amount = Integer.parseInt( args[2] );
                        if (temp.tb_withdrawItems(officer, amount) ) {
                            plugin.out(officer,"You have withdrawn "+amount+" "+plugin.econwrapper.getRankupItemName()+
                                    " from your town's bank" ); 
                            temp.checkTownBank(officer);
                        } else {
                            plugin.out( sender,"The town bank didn't have enough to withdraw" );
                        }
                    } else if (args[1].equalsIgnoreCase("check") || args[1].equalsIgnoreCase("c") ){
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
        }
        
        // Mayor-only commands from here on out
        if (!plugin.econwrapper.hasPerm(officer, "muni.mayor")){
            officer.sendMessage("You do not have permission to do /mayor subcommands"); 
            return true; 
        } 
        
        if (args[0].equalsIgnoreCase("found") || 
                args[0].equalsIgnoreCase("charter") ||args[0].equalsIgnoreCase("add")) {
            if (args.length != 2) {
                officer.sendMessage("/mayor found <TownName>");
                return true;
            }
            if (plugin.isTown(args[1]) ){
                officer.sendMessage("That town already exists.  Please choose another name");
                return true;
            }
            if (plugin.econwrapper.pay(officer, plugin.townRanks[1].getMoneyCost(),
                    plugin.townRanks[1].getItemCost(), "Found: "+args[1] ) ){
                Town t = new Town( plugin, args[1], officer.getName(),officer.getWorld().getName() );
                plugin.towns.put(t.getName(), t );
                plugin.allCitizens.put(officer.getName(), t.getName() );
                t.admin_makeMayor(officer.getName() );
                t.saveToDB();
                officer.sendMessage("You have founded "+t.getName());
                plugin.getServer().broadcastMessage(t.getName()+" is now an official "+
                        t.getTitle()+" thanks to the new mayor " +t.getMayor()+"!" );
            } else { officer.sendMessage("Could not start the town due to insufficent resources" ); }
            return true;
        } else if (args[0].equalsIgnoreCase("delete")  
                || args[0].equalsIgnoreCase("disband")) {
            // this should verify intention before continuing.
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            temp.removeAllTownCits();            //NPE somewhere
            plugin.removeTown(temp.getName() );  //NPE somewhere
            plugin.getServer().broadcastMessage(temp.getName()+
                    " and all its citizens were removed by the mayor, "+ officer.getName()+"!" );
            return true;
        } else if (args[0].equalsIgnoreCase("deputize")) { // buggy but working on it - 19 Feb 13
            if (args.length != 2) {
                officer.sendMessage("Incorrect number of parameters");
                return false;
            }
            if (!plugin.isCitizen(officer) ){ 
                officer.sendMessage("You are not a citizen anywhere"); 
                return true;
            }
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) ); //throwing NPE - 19 Feb 13
            temp.makeDeputy( args[1] ,officer);
            return true;
            
        } else if (args[0].equalsIgnoreCase("rankup")) { 
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            if ( temp.rankup(officer) ){
                plugin.getServer().broadcastMessage(temp.getName()+" has been ranked to "+
                        temp.getTitle()+" by " + officer.getName() );
            }
            displayHelp( officer, args[0] );
            return true;
            
        }  else if (args[0].equalsIgnoreCase("expand")) { 
            Town temp = plugin.getTown( plugin.getTownName( officer.getName() ) );
            
            if ( temp.isMayor(officer) ){
                if ( plugin.wgwrapper.expandRegion(officer.getWorld().getName(),
                        temp.getName(), args[1], 10)){
                    plugin.getServer().broadcastMessage(temp.getName()+" has expanded its borders ");
                } else { officer.sendMessage("You must specify {n,s,e,w,u,d}"); }
            }
            return true;
            
        } else {
            displayHelp( officer, args[0] );
            return true;
        }
    }
    private void displayHelp(CommandSender sender, String subcmd){ 
        if (subcmd.equalsIgnoreCase("deputy") ){
            plugin.out(sender, "Muni Deputy Help.  You can do these commands:",ChatColor.LIGHT_PURPLE);
            plugin.out(sender, "/deputy invite <playerName>");
            plugin.out(sender, "/deputy accept <playerName");
            plugin.out(sender, "/deputy decline <playerName>");
            plugin.out(sender, "/deputy kick <playerName>");
            plugin.out(sender, "/deputy resign");
            //plugin.out(player, "/deputy regions");
            //plugin.out(player, "/deputy setRegion");
            plugin.out(sender, "/deputy setTax <money>");
            plugin.out(sender, "/deputy setItemTax <sponges>");
            //plugin.out(player, "**/deputy pushBorder");
            plugin.out(sender, "**/deputy bank deposit");
            plugin.out(sender, "** (with perm) ");
        } else if (subcmd.equalsIgnoreCase("mayor") ){
            plugin.out(sender, "Muni Mayor Help.  You can do these commands:",ChatColor.LIGHT_PURPLE);
            plugin.out(sender, "/mayor bank");
            plugin.out(sender, "/mayor bank check");
            plugin.out(sender, "/mayor bank deposit <amount>");
            plugin.out(sender, "/mayor bank withdraw <amount>");
            plugin.out(sender, "/mayor deputize <citizen>");
            plugin.out(sender, "/mayor resign");
            plugin.out(sender, "/mayor delete");
            plugin.out(sender, "/mayor rankup");
            plugin.out(sender, "***Mayors may also do all the deputy commands (/deputy help)");
        }
    }
}
