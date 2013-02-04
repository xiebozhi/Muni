package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
//import net.milkbowl.vault.permission.Permission
;import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.ChatColor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;
import java.util.Calendar;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;

/**
 * Muni.java: Startup and shutdown for the Muni plugin
 * 
 * @author bobbshields
 */
public class Muni extends JavaPlugin {
    
    protected static WorldGuardPlugin wgp;
    protected static WGWrapper wgwrapper = null;
            
    protected static Economy economy = null;
    protected static EconWrapper econwrapper = null;
    
    protected dbWrapper dbwrapper = null;

    //Config file path  (Believe this is not needed, testing)
    //private static final String MUNI_DATA_FOLDER = "plugins" + File.separator + "Muni";
    //private static final String MUNI_CONFIG_PATH = MUNI_DATA_FOLDER + File.separator + "config.yml";
   
    //Global options to be pulled from config
    private static double CONFIG_VERSION = .01;
    private static boolean DEBUG = true;
    
    protected boolean useMYSQL = false;
    private String db_host = "jdbc:sqlite://localhost:3306/defaultdb";
    private String db_database = "defaultdatabase";
    protected String db_user = "defaultuser";
    protected String db_pass = "defaultpass"; 
    protected String db_prefix = "defaultpass"; 
    protected String db_URL = null;
    
    protected double maxTaxRate = 10000;
    protected int rankupItemID = 19;
    protected double maxTBbal = -1;
    protected int totalTownRanks = 5;
    
    protected TownRank [] townRanks;
    //protected Town towns = null;
    
    protected TreeSet<Town> towns = new TreeSet<Town>();
    protected TreeSet<Citizen> citizens = new TreeSet<Citizen>();
    //protected ArrayList<Town> towns = new ArrayList<Town>();
    //protected ArrayList<Citizen> citizens = new ArrayList<Citizen>();

    @Override
    public void onDisable() {
        getLogger().info("Shutting Down");
        
        //Save Citizens and towns
        saveCitizens();
        saveTowns();
        
        // Save the config to file
        this.saveConfig();
        
        getLogger().info("Shut Down sequence complete");
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Up");
        
        // Hooks in Vault Economy regions
        // Hooks in World Guard
        // Initializes Muni wrappers
        hookInDependencies();
        
        //Load the configuration file
        this.saveDefaultConfig();
        this.loadConfigSettings();
        
        // Register a new listener
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void playerJoin(PlayerLoginEvent event) {
                // Will get changed to updating last login for citizens
                event.getPlayer().sendMessage("[Muni] Login Message: "+event.getEventName() );
                // if town mayor, show applicants
                // if invitee, display
            }
        }, this);

        // Register Muni commands
        getCommand("town"     ).setExecutor(new TownCommand     (this) );
        getCommand("deputy"   ).setExecutor(new OfficerCommand  (this) );
        getCommand("mayor"    ).setExecutor(new OfficerCommand  (this) );
        getCommand("townadmin").setExecutor(new TownAdminCommand(this) );
        
        //Just testing
        this.getLogger().info( Calendar.getInstance().getTime().toString() );
        
        boolean runTest = false;
        if (runTest){ // this is for testing purposes only, will be deleted soon 
            // Make sure the database tables are there.  
            // Passing true drops the db first, normally false
            this.getLogger().warning("Dropping database!");
            dbwrapper.createDB(true);
            makeDefaultTowns();
            makeDefaultCitizens();
        }
        // Ensure the database is there
        dbwrapper.createDB(false);
        
        this.getLogger().info ("Loading Towns from database");
        loadTowns();
        
        this.getLogger().info ("Loading Citizens from database");
        loadCitizens();

        this.getLogger().info ("Loaded and Ready for Town administration" );
    }// end: onEnable()
    
    /**
     * Queries DB for player names then constructor each loads db at array addition
     * 
     * @author bobbshields
     */
    public void loadCitizens(){
        try{
            Iterator itr = dbwrapper.getSingleCol("citizens","playerName").iterator();
            if ( isDebug() ) { this.getLogger().info(" Loading Citizens. " ); }
            while ( itr.hasNext() ){
                String current = itr.next().toString();
                if ( isDebug() ) { this.getLogger().info("Loading citizens: " + current); }
                citizens.add( new Citizen( this, current ) );
          }
        } catch (NullPointerException ex){
            this.getLogger().severe("Loading citizens: "+ex.getMessage() );
        } finally {
            if ( isDebug() ) { this.getLogger().info("Finshed loading Citizens"); }
        }
    }
    /**
     * Queries DB for town names then town constructor loads itself 
     * 
     * @author bobbshields
     */
    public void loadTowns(){
            Town copyTown = new Town (this);
        try{
            if ( isDebug() ) { this.getLogger().info("Towns Loading. " ); }
            
            for (String curr : dbwrapper.getSingleCol("towns", "townName") ){
                if ( isDebug() ) { this.getLogger().info("Loading town: " + curr); }
                //Town copyTown = new Town (this);
                copyTown.loadFromDB( curr );
                towns.add( new Town ( copyTown ) );
            }
        } catch (NullPointerException ex){
            this.getLogger().severe("Loading towns: "+ex.getMessage() );
        } finally {
            if ( isDebug() ) { this.getLogger().info("Finshed loading Towns"); }
        }
    }
    /* For testing only, will be deleted closer to the beta
     * 
     */
    public void makeDefaultTowns(){
        this.getLogger().info ("Making test towns");
        Town maker = new Town(this);
        maker = new Town (this,"TestTown","bobbshields");
        maker.setMaxDeputies(5); maker.setRank(0);
        maker.setTaxRate(10.5);
        
        maker.saveToDB();
        
        maker = new Town (this,"SecondTest","astickynote",2,1000,100);
        maker.db_addTown();
        
        maker.loadFromDB("TestTown");
        this.getLogger().warning("Load: "+maker.toDB_UpdateRowVals() );
        
        maker = new Town (this,"SecondTest");
        this.getLogger().warning("Load: "+maker.toDB_UpdateRowVals() );
    }
    
    public void makeDefaultCitizens(){
        this.getLogger().info ("Making test citizens");
        Citizen maker = new Citizen(this);
        maker = new Citizen(this,"TestTown","bobbshields",true, false, false, false,null);
        maker.saveToDB();
        maker = new Citizen(this,"TestTown","tlunarrune",false, false, true, false,null);
        maker.saveToDB();
        maker = new Citizen(this,"TestTown","themoltenangel",false, false, false, true,"bobbshields");
        maker.saveToDB();
        maker = new Citizen(this,"SecondTest","astickynote", true, false, false, false, null);
        maker.saveToDB();
        
    }
    public void saveCitizens(){
        for (Citizen curr : citizens){
            curr.saveToDB();
        }
    }
    public void saveTowns() {
        for (Town curr: towns) {
            curr.saveToDB();
        }
    }
    public void addTown( Town addition ) {
        towns.add(addition);
    }
    public void addCtizien (Citizen addition) {
        citizens.add(addition);
    }
    public void removeTown(String town_Name){
        Town temp = new Town (this,town_Name);
        if (towns.contains(temp) ){
            towns.remove(temp);
        }
    }
    public void removeCitizen(String player){
        Citizen temp = new Citizen (this, player);
        if (citizens.contains(temp) ) {
            citizens.remove(temp);
        }
    }
    public Town getTown(String town_Name){
        Town temp = null;
        for (Town curr: towns) {
            if (curr.getName().equals(town_Name) ){
                temp = curr;
            } 
        }
        return temp;
    }
    public Town getTown(Player player){
        Town temp = null;
        String search = getCitizen(player.getName() ).getTown();
        for (Town curr: towns) {
            if (curr.getName().equals(search) ){
                temp = curr;
            } 
        }
        return temp;
    }
    public Citizen getCitizen(String player){
        Citizen temp = null;
        for (Citizen curr: citizens){
            if (curr.getName().equals(player) ){
                temp = curr;
            }
        }
        return temp;
    }
    public String getAllTowns(){
        String temp = "";
        for (Town curr: towns) {
            temp = temp + curr.getName() +", ";
        }
        return temp;
    }
    public String getAllCitizens(){
        String temp = null;
        for (Citizen curr: citizens){
            temp = temp + curr.getName() +", ";
        }
        return temp;
    }

    private void hookInDependencies() {
        try {
            wgp = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
            wgwrapper = new WGWrapper(this);
        } catch (Exception e) {
            getLogger().severe( "[Muni] Error occurred in hooking in to WorldGuard. Are both WorldGuard and WorldEdit installed?");
            getLogger().severe( "[Muni] !!!!!NOTICE!!!!! MUNI WILL NOW BE DISABLED.  !!!!!NOTICE!!!!!");
            this.getPluginLoader().disablePlugin(this);
        }

        try {
            boolean Econ_success = setupEconomy();
            if (!Econ_success) {
                getLogger().severe( "Muni: Unable to hook-in to Vault (Econ)!");
            }
        } catch (Exception e) {
            getLogger().severe( "Muni: Unable to hook-in to Vault: "+e.getMessage());
            getLogger().severe("[Muni] !!!!!NOTICE!!!!! MUNI WILL NOW BE DISABLED.  !!!!!NOTICE!!!!!");
            this.getPluginLoader().disablePlugin(this);
        }
        dbwrapper = new dbWrapper(this);
        if ( isDebug() ) { getLogger().info( "Dependancies Hooked"); }
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        econwrapper = new EconWrapper(this);
        return (economy != null );
    }

   protected void loadConfigSettings(){
        if (CONFIG_VERSION != this.getConfig().getDouble("config_version") ){
            getLogger().warning("Config version does not match software requirements.");
        }
        DEBUG = this.getConfig().getBoolean("debug");
        
        useMYSQL = this.getConfig().getBoolean("database.use-mysql");
        db_host = this.getConfig().getString("database.host");
        db_database = this.getConfig().getString("database.database");
        db_user = this.getConfig().getString("database.user");
        db_pass = this.getConfig().getString("database.password");
        db_prefix = this.getConfig().getString("database.prefix");
        
        // Format the URL from the private variables
        db_URL = useMYSQL ? "jdbc:mysql"+"://"+ db_host +":3306/"+db_database 
                : "jdbc:sqlite:plugins/Muni/"+db_database+".db";
        
        if ( isDebug() ) {getLogger().info("dbURL = " + db_URL); }
                    
        maxTaxRate = this.getConfig().getDouble("townsGlobal.maxTaxRate"); 
        rankupItemID = this.getConfig().getInt("townsGlobal.rankupItemID");    
        maxTBbal = this.getConfig().getDouble("townsGlobal.maxTownBankBalance");  
        totalTownRanks = this.getConfig().getInt("townsGlobal.maxRanks"); 

        if ( isDebug() ) {getLogger().info( maxTaxRate + " " + rankupItemID +
                " " + maxTBbal + " " + totalTownRanks ); }
        
        townRanks = new TownRank [totalTownRanks+1];
        for ( int i=1; i <= totalTownRanks; i++ ){
            townRanks[i] = new TownRank( i,
                    this.getConfig().getString("townRanks."+(i)+".title"),
                    this.getConfig().getInt   ("townRanks."+(i)+".maxDeputies"),
                    this.getConfig().getInt   ("townRanks."+(i)+".minCitizens"),
                    this.getConfig().getInt   ("townRanks."+(i)+".maxCitizens"),
                    this.getConfig().getDouble("townRanks."+(i)+".moneyCost"),
                    this.getConfig().getInt   ("townRanks."+(i)+".itemCost") );
                    if ( isDebug() ) { getLogger().info( townRanks[i].getName()+
                            " config settings were loaded"); }
        }
        if ( isDebug() ) {getLogger().info("Config settings loaded"); }
        
   }
   
   public boolean isDebug() { return DEBUG; }
   public void setDebug(boolean value){ 
       DEBUG = value; 
       this.getLogger().info("Debug changed to: " + String.valueOf(value) );
   }
   //public boolean isMySQL() { return useMYSQL; }
    
}
