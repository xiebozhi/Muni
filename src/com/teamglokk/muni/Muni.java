package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
//import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.InputStream;

/**
 * Muni.java: Startup and shutdown for the Muni plugin
 * 
 * @author bobbshields
 */
public class Muni extends JavaPlugin {
    //private final SamplePlayerListener playerListener = new SamplePlayerListener(this);
    //private final SampleBlockListener blockListener = new SampleBlockListener();
    //private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    protected static WorldGuardPlugin wgp;
    protected static WGWrapper wgwrapper = null;
            
    protected static Economy economy = null;
    protected static EconWrapper econwrapper = null;

    //Config file path
    private static final String MUNI_DATA_FOLDER = "plugins" + File.separator + "Muni";
    private static final String MUNI_CONFIG_PATH = MUNI_DATA_FOLDER + File.separator + "config.yml";
    //private static Config options;

    //Global options to be pulled from config
    private static double CONFIG_VERSION = .01;
    private static boolean DEBUG = true;
    
    protected boolean useMYSQL = false;
    protected String db_host = "jdbc:sqlite://localhost:3306/defaultdb";
    protected String db_database = "defaultdatabase";
    protected String db_user = "defaultuser";
    protected String db_pass = "defaultpass"; 
    protected String db_prefix = "defaultpass"; 
    
    protected double maxTaxRate = 10000;
    protected int rankupItemID = 19;
    protected double maxTBbal = -1;
    protected int totalTownRanks = 5;
    
    protected TownRank [] townRanks;
    protected Set<Town> towns = null;

    @Override
    public void onDisable() {
        getLogger().info("Shutting Down");
        
        //save all towns to database 
        
        // Save the config to file
        // this.saveConfig();
        
        getLogger().info("Shut Down sequence ended");
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Up");
        
        // Hooks in Vault Economy regions
        // Hooks in World Guard
        hookInDependencies();
        
        //Load the configuration file
        this.saveDefaultConfig();
        loadConfigSettings();
        
        // Register a new listener
        /*
        getServer().getPluginManager().registerEvents(new Listener() {
 
            @EventHandler
            public playerJoin(PlayerJoinEvent event) {
                // On player join send them the message from config.yml
                event.getPlayer().sendMessage(SimpleMOTD.this.getConfig().getString("message"));
            }
        }, this);
        */
        
        // Register our events
        //PluginManager pm = getServer().getPluginManager();
        //pm.registerEvents(playerListener, this);
        //pm.registerEvents(blockListener, this);

        // Register Muni commands
        getCommand( "town"     ).setExecutor(new TownCommand(this));
        getCommand("deputy"    ).setExecutor(new OfficerCommand(this) );
        getCommand("mayor"     ).setExecutor(new OfficerCommand(this) );
        getCommand("townadmin" ).setExecutor(new TownAdminCommand(this) );
        
        //parse the config files for the town rank definitions and push to 
        // TownRank (int id, String name, int max_Deputies, int min_Citizens, int max_Citizens, double money_Cost, int item_Cost )
        // for each of the town ranks

        //Load the towns into memory from the database 
        // for (database results) {add all town data to the towns set} 
        /* townname
         * tax rate
         * mayor
         * deputies
         * citizens
         * invitees
         * applicants
         */
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
                getLogger().severe( "Muni: Unable to hook-in to Vault.");
                getLogger().severe("[Muni] !!!!!NOTICE!!!!! MUNI WILL NOW BE DISABLED.  !!!!!NOTICE!!!!!");
                this.getPluginLoader().disablePlugin(this);
            }
        if ( DEBUG ) { getLogger().info( "Dependancies Hooked"); }
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
        return economy != null;
    }

   protected void loadConfigSettings(){
        CONFIG_VERSION = this.getConfig().getDouble("config_version");
        DEBUG = this.getConfig().getBoolean("debug");
        
        useMYSQL = this.getConfig().getBoolean("database.use-mysql");
        db_host = this.getConfig().getString("database.url");
        db_database = this.getConfig().getString("database.database");
        db_user = this.getConfig().getString("database.user");
        db_pass = this.getConfig().getString("database.password");
        db_prefix = this.getConfig().getString("database.prefix");
        
            
        maxTaxRate = this.getConfig().getDouble("townsGlobal.maxTaxRate"); 
        rankupItemID = this.getConfig().getInt("townsGlobal.rankupItemID");    
        maxTBbal = this.getConfig().getDouble("townsGlobal.maxTownBankBalance");  
        totalTownRanks = this.getConfig().getInt("townsGlobal.maxRanks"); 

        townRanks = new TownRank [totalTownRanks+1];
        for ( int i=0; i <= totalTownRanks; i++ ){
            townRanks[i+1] = new TownRank( i+1,
                    this.getConfig().getString("townRanks"+ (i+1)+"title"),
                    this.getConfig().getInt("townRanks"+ (i+1)+"maxDeputies"),
                    this.getConfig().getInt("townRanks"+ (i+1)+"minCitizens"),
                    this.getConfig().getInt("townRanks"+ (i+1)+"maxCitizens"),
                    this.getConfig().getDouble("townRanks"+ (i+1)+"moneyCost"),
                    this.getConfig().getInt("townRanks"+ (i+1)+"itemCost") 
                    );
        }
   }
   
   public boolean isDebug() { return DEBUG; }
   public boolean isMySQL() { return useMYSQL; }
    
}
