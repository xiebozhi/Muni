/**
 * Muni.java: Startup and shutdown for the Muni plugin
 * 
 * @author bobbshields
 */

package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.permission.Permission;

import com.teamglokk.muni.WGWrapper;
import com.teamglokk.muni.Town;

/**
 * Sample plugin for Bukkit
 *
 * @author Dinnerbone
 */
public class Muni extends JavaPlugin {
    //private final SamplePlayerListener playerListener = new SamplePlayerListener(this);
    //private final SampleBlockListener blockListener = new SampleBlockListener();
    //private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    protected static WorldGuardPlugin wgp;
    protected static WGWrapper wgwrapper = null;
            
    protected static Economy economy = null;
    protected static EconWrapper econwrapper = null;
    
    protected static Permission perms = null;

    //Config file path
    private static final String MUNI_DATA_FOLDER = "plugins" + File.separator + "Muni";
    private static final String MUNI_CONFIG_PATH = MUNI_DATA_FOLDER + File.separator + "config.yml";
    //private static Config options;

    //Global options to be pulled from config
    protected double maxTaxRate = 10000;
    protected boolean useMYSQL = false;
    protected String db_url = "jdbc:mysql://localhost:3306/defaultdb";
    protected String db_user = "defaultuser";
    protected String db_pass = "defaultpass"; 
    
    private static boolean DEBUG = false;
    private static boolean USEMYSQL = false;
    
    protected int totalTownRanks = 5;
    private TownRank [] townRanks;
    
    protected Set<Town> towns = null;

    @Override
    public void onDisable() {
        getLogger().info("Shutting Down");
        //save all towns to database 
        getLogger().info("Shut Down sequence ended");
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Up");
        hookInDependencies();
        
        // Register our events
        //PluginManager pm = getServer().getPluginManager();
        //pm.registerEvents(playerListener, this);
        //pm.registerEvents(blockListener, this);

        // Register Muni commands
        getCommand( "town"   ).setExecutor(new TownCommand(this));
        //getCommand("deputy").setExecutor(new OfficerCommand(this) );
        //getCommand("mayor" ).setExecutor(new OfficerCommand(this) );
        
        //Load the configuration file
        // set the DEBUG variable
        //Give the static database wrapper its connection parameters
        
        // find the total number of town ranks from config:  totalTownRanks = 5;
        // townRanks = new TownRank [totalTownRanks];
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
                boolean Perm_success = setupPermissions();
                if (!Econ_success) {
                    getLogger().severe( "Muni: Unable to hook-in to Vault (Econ)!");
                }
                else if (!Perm_success) {
                    getLogger().severe( "Muni: Unable to hook-in to Vault (Perm)!");
                }
            } catch (Exception e) {
                getLogger().severe( "Muni: Unable to hook-in to Vault.");
                getLogger().severe("[Muni] !!!!!NOTICE!!!!! MUNI WILL NOW BE DISABLED.  !!!!!NOTICE!!!!!");
                this.getPluginLoader().disablePlugin(this);
            }
        if (/*getDebug()*/ true ) { getLogger().info( "Dependancies Hooked"); }
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
    
   private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
   
   public boolean isDebug() { return DEBUG; }
   public boolean isMySQL() { return USEMYSQL; }
    
}
