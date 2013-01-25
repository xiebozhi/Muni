
package com.teamglokk.muni;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.economy.Economy;
//import com.teamglokk.muni.SampleBlockListener;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
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
    protected static EconWrapper econwrapper = null;
    protected static Permission perms = null;
    protected static Economy economy = null;
    protected Town [] towns = null;
    //private static final String MUNI_DATA_FOLDER = "plugins" + File.separator + "Muni";
    //private static final String MUNI_TEXT_CONFIG_PATH = MUNI_DATA_FOLDER + File.separator + "config.txt";
    //private static Config options;

    @Override
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        getLogger().info("Shutting Down");
    }

    @Override
    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events

        // Register our events
        //PluginManager pm = getServer().getPluginManager();
        //pm.registerEvents(playerListener, this);
        //pm.registerEvents(blockListener, this);

        getLogger().info("Starting Up");
        hookInDependencies();
        
        // Register our commands
        getCommand("town").setExecutor(new TownCommand(this));
        //getCommand("debug").setExecutor(new SampleDebugCommand(this));
        //getCommand("pos").setExecutor(new SamplePosCommand());

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
/*
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    * 
    * /* If you're having these methods in your plugin's main class (which extends JavaPlugin), you can remove parameters plugin from them,
    * and in the FixedMetadataValue constructor and getMetadata method, use "this" instead* /
    public void setMetadata(Player player, String key, Object value, Plugin plugin){
      player.setMetadata(key,new FixedMetadataValue(plugin,value));
    }
    public Object getMetadata(Player player, String key, Plugin plugin){
      List<MetadataValue> values = player.getMetadata(key);  
      for(MetadataValue value : values){
         if(value.getOwningPlugin().getDescription().getName().equals(plugin.getDescription().getName())){
            return value.value();
         }
      }
    }
    */
    private void hookInDependencies() {
        try {
            wgp = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
            wgwrapper = new WGWrapper(this);
        } catch (Exception e) {
            getLogger().severe( "[Muni] Error occurred in hooking in to WorldGuard. Are both WorldGuard and WorldEdit installed?");
            getLogger().severe("[Muni] !!!!!NOTICE!!!!! MUNI WILL NOW BE DISABLED.  !!!!!NOTICE!!!!!");
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
     
    
}
