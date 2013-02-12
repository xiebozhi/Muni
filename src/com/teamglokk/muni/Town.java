package com.teamglokk.muni;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.TreeSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Town.java: defines the Town class
 * @author bobbshields
 */
public class Town implements Comparable<Town> {
    // Gives access to global vars and functions
    private static Muni plugin;
    
    // Database table is $db_prefix_ towns
    //private int db_pk;
    private String townName;
    //private Location townCenter;   
    private double townBankBal;
    private double taxRate;
    private int townRank;
     
	// Stored in (prefix)_citizens	TreeSet<Citizen> citizens = new TreeSet<Citizen>();
    private String townMayor;
    protected Citizen mayor = null;
    protected TreeSet<Citizen> deputies = new TreeSet<Citizen>();
    protected TreeSet<Citizen> citizens = new TreeSet<Citizen>();
            
    public boolean makeMayor(Player player){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a mayor of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return true;
        }
        if ( isDeupty(player) || isCitizen(player) ){
            if (mayor.getName().equalsIgnoreCase( "empty" ) ){
               if ( plugin.econwrapper.hasPerm( player, "muni.mayor" ) ){
                    mayor = new Citizen ( plugin, player );
                    saveToDB();
                    return true;
                } else { player.sendMessage("You do not have permission to become a mayor."); }
            } else {player.sendMessage("There is already a mayor") ;}
        } else {player.sendMessage("You're not a citizen of the town" ); }
        return true;
    }
    public boolean resignMayor (){
        mayor.setName("empty");
        // log a transaction here
        return true;
    }
    public boolean resignDeputy ( Player player ){
        deputies.remove(new Citizen (plugin,player) );
        saveToDB();
        // log a transaction here
        return true;
    }
    public boolean removeCitizen ( Player player ){
        citizens.remove(new Citizen (plugin,player) );
        saveToDB();
        // log a transaction here
        return true;
    }
    public boolean makeDeputy(Player player){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a deputy of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return false;
        } else if ( isDeupty(player ) ){
            player.sendMessage("You are already a deputy of "+townName);
            return false;
        } else if ( isCitizen(player) ){
            if ( plugin.townRanks[townRank].maxDeputies > deputies.size() ){ // might have to adjust array...
                if ( plugin.econwrapper.hasPerm(player, "muni.deputy")
                        || plugin.econwrapper.hasPerm(player, "muni.deputy") ){
                    deputies.add(new Citizen (plugin,player) );
                    saveToDB();
                    return true;
                } else { player.sendMessage("You do not have permission to become a deputy."); }
            } else { player.sendMessage("Too many deputies, rank up or fire someone"); }
        } else { player.sendMessage("You are not a member of "+ townName); }
        return false;
    }
    public boolean makeCitizen(Player player){
        if ( !plugin.getServer().getPlayer(player.getName() ).isOnline() ){
            plugin.getLogger().warning("Player "+player.getName()+" is not online to make a citizen of " +townName );
            return false;
        }
        if ( isMayor(player) ){
            player.sendMessage("You are already the mayor of "+townName);
            return false;
        } else if ( isDeupty(player ) ){
            player.sendMessage("You are already a deputy of "+townName);
            return false;
        } else if ( plugin.allCitizens.containsValue( player.getName() ) ) {
            player.sendMessage("You are already a member of a town.");
            return false;
        } else if ( plugin.townRanks[townRank].maxCitizens > deputies.size() + citizens.size() + 1 ){ // might have to adjust array...
                if ( plugin.econwrapper.hasPerm(player, "muni.citizen") ){
                    citizens.add(new Citizen (plugin,player) );
                    saveToDB();
                    return true;
                } else { player.sendMessage("You do not have permission to become a citizen."); }
        } else { player.sendMessage(townName+" has too many citizens.  Wait for a vacancy"); }
        return false;
    }
    
    public boolean isMayor( Player player ){
        if ( player.getName().equalsIgnoreCase( mayor.getName() ) ){
            return true;
        } else { return false; }
    }
    public boolean isDeupty( Player player ){
        if (deputies.contains( new Citizen(plugin, player.getName()) ) ){
            return true;
        } else { return false; }
    }
    public boolean isCitizen(Player player){
        if (citizens.contains( new Citizen(plugin, player.getName()) ) ){
            return true;
        } else { return false; }
    }
    public void loadCitizens(){
        try{
            Iterator itr = plugin.dbwrapper.getTownCits( townName ).iterator();
            if ( plugin.isDebug() ) { plugin.getLogger().info(" Loading Citizens. " ); }
            while ( itr.hasNext() ){
                String current = itr.next().toString();
                if ( plugin.isDebug() ) { plugin.getLogger().info("Loading citizens: " + current); }
                citizens.add( new Citizen( plugin, current ) ); //constructor loads from DB
                if ( citizens.last().isMayor() ) {
                    mayor = citizens.last();
                    citizens.remove(citizens.last() );
                }
                if ( citizens.last().isDeputy() ) {
                    deputies.add(citizens.last() );
                    citizens.remove(citizens.last() );
                }
          }
        } catch (NullPointerException ex){
            plugin.getLogger().severe("Failed to load citizens for town ("+ townName +"): "+ex.getMessage() );
        } finally {
            if ( plugin.isDebug() ) { plugin.getLogger().info("Finshed loading Citizens"); }
        }
    }
    private int maxDeputies = 5;
    
    private Timestamp createdDate;
    
    public Town (Muni instance){
        plugin = instance;
    }
    public Town (Town copy){
        townName = copy.getName();
        townMayor = copy.getMayor(); 
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        taxRate = copy.getTaxRate();
    }
   public Town (Muni instance, String town_Name, String player ){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Town with mayor: "+town_Name+", "+player);
        townName = town_Name;
        townMayor = player; 
        townRank = 1;
        townBankBal = 5;
        taxRate = 10;
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }
    /**
     * Full data constructor
     * 
     * @author bobbshields
     */
    public Town (Muni instance, String town_Name, String mayor,
            int rank, double bankBal, double tax){
        
        plugin = instance;
        if (plugin.isDebug() ) plugin.getLogger().info("Begin Muni Constructor: "+mayor+", "+town_Name);
        townName = town_Name;
        townMayor = mayor; 
        townRank = rank;
        townBankBal = bankBal;
        taxRate = tax;
        if (plugin.isDebug() ) plugin.getLogger().info("End Muni Constructor: "+toDB_Vals() );
        
    }
    /* Loads the class instance variables from the database using the passed town name.
     * 
     * @author bobbshields
     */
    public Town (Muni instance, String town_Name ){
        
        plugin = instance;
        loadFromDB(town_Name);
        
    }    
    public boolean loadFromDB(String town_Name){
        Town copy = plugin.dbwrapper.getTown(town_Name);
        //plugin = copy.plugin;
        townName = copy.getName();
        townMayor = copy.getMayor(); 
        townRank = copy.getRank();
        townBankBal = copy.getBankBal();
        taxRate = copy.getTaxRate();
        return true;
    }    
    public boolean saveToDB(){
        // if exists, update; else insert
        //db_updateRow(String table, String key_col, String key, String colsANDvals
        if ( plugin.dbwrapper.checkExistence("towns", "townName", townName) ){
            return plugin.dbwrapper.updateRow("towns", "townName", townName, toDB_UpdateRowVals());
        } else {
            return plugin.dbwrapper.insert("towns", toDB_Cols(), toDB_Vals() );
        }
    }
    public String toDB_Cols(){
        return "townName,mayor, townRank,bankBal,taxRate";
    }
    public String toDB_Vals(){
        return "'"+townName +"','"+townMayor+"','"+
               Integer.toString(townRank) +"','"+
               Double.toString(townBankBal) +"','"+ Double.toString(taxRate)+"'";
    }  
    public String toDB_UpdateRowVals(){
        return "townName='"+townName+"', townRank='"+townRank+"', bankBal='"+
                Double.toString(townBankBal)+"', taxRate='"+Double.toString(taxRate)+"' ";
    }
    public String info(){
        return toDB_Vals();
    }
    public boolean db_addTown(Player mayor, String town_Name){
        if ( !plugin.econwrapper.payMoney(mayor,1000) ){
            mayor.sendMessage("Not enough money to found the town");
            return false;
        }
        townName = town_Name;
        townMayor = mayor.getName(); 
        townBankBal = 0;
        taxRate = 10;
        
        if (!plugin.dbwrapper.checkExistence("towns","townName", townName) ) {
            plugin.dbwrapper.insert("towns",toDB_Cols(),toDB_Vals() );
            return true;
        } else {
            return false;
        }
    }
    public boolean db_addTown(){
        if (!plugin.dbwrapper.checkExistence("towns","townName", townName) ) {
            plugin.dbwrapper.insert("towns",toDB_Cols(),toDB_Vals() );
            return true;
        } else {
            return false;
        }
    }
    public boolean setMaxDeputies(int max){
        maxDeputies = max;
        return true;
    }
    public int getMaxDeputies() {
        return maxDeputies; 
    }
    public String getMayor(){
        return townMayor;
    }
    public boolean setMayor(String mayor){
        townMayor = mayor;
        return true;
    }
    public boolean rankup(Player player){
        double rankCost = plugin.townRanks[townRank+1].getMoneyCost();
        int rankCostItem = plugin.townRanks[townRank+1].getItemCost();
        
        if ( rankCost >= townBankBal ){
            if (plugin.econwrapper.payItemR( player, plugin.rankupItemID, rankCostItem,"Rankup" ) ) {
                player.sendMessage("You have successfully ranked "+ townName +" to level " + (++townRank) );
                return payFromTB(rankCost); //Payment of money taken after sponges are confirmed
            } else{ 
                player.sendMessage("You do not have enough "+"Sponges"+" in your inventory to rank up "+ townName);
                return false;
            }
        }else {
            player.sendMessage("You need to depsoit "+ (rankCost-townBankBal) +" into the town bank to rank up "+ townName);
            return false;
        }  
    }
    public boolean payFromTB (Double amount){
        if ( townBankBal >= amount ){
                townBankBal = townBankBal - amount;
            return true;
        }else{
            return false;
        }
    }
    public int getRank(){
        return townRank;
    }
    public boolean setRank(int rank){
        if (rank >= 0 && rank < 10){
            townRank = rank;
            return true;
        }else{ return false; }
    }
    public boolean tb_deposit(Player player, double amount){
        if ( plugin.econwrapper.payMoney(player,amount) ){
            townBankBal = townBankBal + amount;
            return true;
        } else {return false; }
    }
    public boolean tb_withdrawl(Player player, double amount){
        if ( townBankBal >= amount ){
            if (plugin.econwrapper.giveMoney(player,amount) ) {
                townBankBal = townBankBal - amount;
                return true;
            } else { return false; }
        } else { return false; }
    }
    public boolean payTaxes(Player player){
        if (payTaxes(player, taxRate ) ){
            return true;
        } else { return false; }
    }
    public boolean payTaxes(Player player, Double amount){
        if ( plugin.econwrapper.pay(player, amount, 0, "Taxes for "+townName ) ){
            townBankBal = townBankBal + amount;
            //player.sendMessage("You have paid your taxes to "+townName+" of amount "+ amount+" "+plugin.econwrapper.getCurrName(amount) );
            //Transaction t = new Transaction ( )
            return true;
        } else { return false; }
    }
    public double getBankBal (){
        return townBankBal;
    }
    public double getTaxRate(){
        return taxRate;
    }
    public boolean setTaxRate(double rate){
        // verify the rate is above 0 and below the hard max
        if (rate < plugin.maxTaxRate && rate > 0){
            taxRate = rate;
            return true;
        } else{
            return false;
        }
    }
    public String getName(){
        return townName;
    }
    public boolean setName(String name){
        townName = name;
        return true;
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31). 
            append(townName).toHashCode();
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
    @Override
    public String toString(){
        return townName;
    }
    @Override
    public int compareTo(Town t){
        return this.getName().toLowerCase().compareTo( t.getName().toLowerCase() );
        /*
        if (this.getRank() < t.getRank() ){
            return -1;
        } else if (this.getRank() > t.getRank() ){
            return 1;
        } else if (this.getRank() == t.getRank() ){
            return this.getName().toLowerCase().compareTo( t.getName().toLowerCase() );
        } else { return 0;}
        */
    }
    
    /*
    public String getDeputies(){
        String temp = null;
        Iterator itr = townDeputies.iterator();
         while(itr.hasNext() ){
             temp = temp + itr.next() +", ";
         }
         return temp;
    }
    public boolean addDeputy(String deputy){
        townDeputies.add(deputy);
        return true;
    }
    public boolean checkOfficer (String player) {
        if (townMayor.equalsIgnoreCase(player) )
        { return true;
        } else if (Arrays.asList(townDeputies).contains(player) ){
            return true;
        } else {return false;}
    }
    public Location getCenter (){
        return townCenter;
    }
    public boolean setCenter (Location center ){
        if (center != null) {
            townCenter = center;
            return true;
        } else {return false;}
      
    }*/
}
