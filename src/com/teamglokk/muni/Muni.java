package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.ArrayList;
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
    protected ArrayList<Town> towns = new ArrayList<Town>();
    protected ArrayList<Citizen> citizens = new ArrayList<Citizen>();

    @Override
    public void onDisable() {
        getLogger().info("Shutting Down");
        
        //save all towns to database 
        
        // Save the config to file
        // this.saveConfig();
        
        getLogger().info("Shut Down sequence complete");
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Up");
        
        // Hooks in Vault Economy regions
        // Hooks in World Guard
        // Initializes Muni wrappers
        hookInDependencies();
        
        // Always make sure a database is there.  
        // Passing true drops the db first, normally false
        dbwrapper.createDB(false);
        
        //Load the configuration file
        this.saveDefaultConfig();
        loadConfigSettings();
        
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
        getCommand( "town"     ).setExecutor(new TownCommand(this));
        getCommand("deputy"    ).setExecutor(new OfficerCommand(this) );
        getCommand("mayor"     ).setExecutor(new OfficerCommand(this) );
        getCommand("townadmin" ).setExecutor(new TownAdminCommand(this) );
        
        this.getLogger().info( Calendar.getInstance().toString() );
        
        this.getLogger().info (ChatColor.AQUA+"Here 1");
        //Town temp = new Town(this,"bobbshields","test2");
        //temp.db_addTown();
        loadTowns();
        
        this.getLogger().info ("Here 2");
        loadCitizens();
        /*
        try{
            //towns.add( temp );
            //this.getLogger().info ("Here 3" );
            
            
            Iterator itr = dbwrapper.getTowns().iterator();
            this.getLogger().info ("Here 3");
            
            while ( itr.hasNext() ){
                String current = itr.next().toString();
                if ( isDebug() ) { this.getLogger().info(current); }
                towns.add( new Town( this, current ) );
                this.getLogger().info ("Here 3.5" );
            }
        } catch (NullPointerException ex){
            this.getLogger().severe("Adding Towns error: "+ex.getMessage() );
        }
            */
        this.getLogger().info ("Loaded and Ready for Town administration" );
    }
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
        try{
            //towns.add( temp );
            //this.getLogger().info ("Here 3" );
            
            Iterator itr = dbwrapper.getTowns().iterator();
            if ( isDebug() ) { this.getLogger().info("Towns Loading. " ); }
            
            while ( itr.hasNext() ){
                String current = itr.next().toString();
                if ( isDebug() ) { this.getLogger().info("Loading town: " + current); }
                towns.add( new Town( this, current ) );
            }
        } catch (NullPointerException ex){
            this.getLogger().severe("Loading towns: "+ex.getMessage() );
        } finally {
            if ( isDebug() ) { this.getLogger().info("Finshed loading Towns"); }
        }
    }
    public void saveCitizens(){
        
    }
    public void saveTowns() {
        
    }
    public void makeTrans(){
        
    }
    public Town getTown(String town_Name){
        Town temp = null;
        
        return temp;
    }
    public Citizen getCitizen(String player){
        Citizen temp = null;
        
        return temp;
    }
    public String getAllTowns(){
        String temp = null;
        
        return temp;
    }
    public String getAllCitizens(){
        String temp = null;
        
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
   }
   
   public boolean isDebug() { return DEBUG; }
   public void setDebug(boolean value){ 
       DEBUG = value; 
       this.getLogger().info("Debug changed to: " + String.valueOf(value) );
   }
   //public boolean isMySQL() { return useMYSQL; }
    
}
