package com.gmail.llmdlio.townyflight;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.llmdlio.townflight.listeners.PlayerEnterTownListener;
import com.gmail.llmdlio.townflight.listeners.PlayerLeaveTownListener;
import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyFlight extends JavaPlugin {
	
	private final PlayerEnterTownListener playerEnterListener = new PlayerEnterTownListener(this);
	private final PlayerLeaveTownListener playerLeaveListener = new PlayerLeaveTownListener(this);

	public static String pluginPrefix;
	private static String flightOnMsg;
	private static String flightOffMsg;
	private static String noTownMsg;
	private static String notInTownMsg;
	public static String flightDeactivatedMsg;	
	private static String noPermission;
	private static String notDuringWar;
	
	private static Boolean autoEnableFlight;
	public static Boolean autoEnableSilent;
	private static Boolean disableDuringWar;
	
	private TownyFlightConfig config = new TownyFlightConfig(this);
	
	public void onEnable() {
		
    	reloadConfig();  
		
    	if (!LoadSettings()) {
    		getLogger().severe("Config failed to load!");    		
    		this.getServer().getPluginManager().disablePlugin(this);
    		return;
    	}   	
	    	    
    	Plugin towny = getServer().getPluginManager().getPlugin("Towny");
    	if (getServer().getPluginManager().getPlugin("Towny").isEnabled()) {
    		String version = towny.getDescription().getVersion().substring(1, 4);
    		version = version.replace(".","");
    		Integer ver = Integer.parseInt(version);
    		if (ver < 92) {
    			getLogger().severe("Towny version inadequate: 0.92.0.0 or newer required.");    		
				this.getServer().getPluginManager().disablePlugin(this);
				return;
    		}
    	}   		

    	registerEvents();
	    getLogger().info(this.getDescription().getFullName() + " by LlmDl Enabled.");
	}
    
    public void onDisable() {
    	getLogger().info("TownyFlight Disabled.");
    }
    
    public void reloadConfig() { 
        if (!getDataFolder().exists())  
            getDataFolder().mkdirs(); 
        config.reload();       
    }
	
    private boolean LoadSettings() {		
		pluginPrefix = ChatColor.translateAlternateColorCodes('&', config.getConfig().getString("pluginPrefix"));

		// Language Strings
		flightOnMsg = config.getConfig().getConfigurationSection("language").getString("flightOnMsg");
		flightOffMsg = config.getConfig().getConfigurationSection("language").getString("flightOffMsg");
		noTownMsg = config.getConfig().getConfigurationSection("language").getString("noTownMsg");
		notInTownMsg = config.getConfig().getConfigurationSection("language").getString("notInTownMsg");
		flightDeactivatedMsg = config.getConfig().getConfigurationSection("language").getString("flightDeactivatedMsg");
		noPermission = config.getConfig().getConfigurationSection("language").getString("noPermission");
		notDuringWar = config.getConfig().getConfigurationSection("language").getString("notDuringWar");

		// Options
		autoEnableFlight = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Flight").equalsIgnoreCase("true");
		autoEnableSilent = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Silent").equalsIgnoreCase("true");
		disableDuringWar = config.getConfig().getConfigurationSection("options").getString("disable_During_Wartime").equalsIgnoreCase("true");
				
		return true;		
	}
    
    private void registerEvents(){
    	final PluginManager pluginManager = getServer().getPluginManager();
    	HandlerList.unregisterAll(playerEnterListener);
    	if (autoEnableFlight)
    		pluginManager.registerEvents(playerEnterListener, this);
    	pluginManager.registerEvents(playerLeaveListener, this);
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tfly")) {
			if (args.length == 0) {
				if (!(sender instanceof Player))
					return false;
                if (!sender.hasPermission("townyflight.command.tfly")) {
                	sender.sendMessage(pluginPrefix + ChatColor.RED + noPermission + "townyflight.command.tfly");
                	return false;
                }
                if (!canFly((Player) sender))
                	return false;                
                toggleFlight((Player) sender, false, false);
                return true;
	        }
			
			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("townyflight.command.tfly.reload")) {
					sender.sendMessage(pluginPrefix + ChatColor.RED + noPermission + "townyflight.command.tfly.reload");
					return false;
				}
				config.reload();
		    	LoadSettings();
		    	registerEvents();
				sender.sendMessage(pluginPrefix + "Config.yml reloaded");
				return true;
			}
		}
		return false;
    }
    
    /*
     * Take care of whether or not a player can fly here.
     */
    public static boolean canFly(Player player) {
    	try {
			Resident resident = null;
			resident= TownyUniverse.getDataSource().getResident(player.getName());
			if (disableDuringWar)
				if (TownyUniverse.isWarTime()) {
					player.sendMessage(pluginPrefix + notDuringWar);
					return false;
				}
			if (!resident.hasTown()) {
				player.sendMessage(pluginPrefix + noTownMsg);
				return false;
			}        
			if (TownyUniverse.isWilderness(player.getLocation().getBlock())) {
				player.sendMessage(pluginPrefix + notInTownMsg);
				return false;
			}        
			Town town = null;
			town = TownyUniverse.getTownBlock(player.getLocation()).getTown();
			if (!resident.getTown().equals(town)) {
				player.sendMessage(pluginPrefix + notInTownMsg);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;    	
    }

    /*
     * If flight is on, turn it off and vice versa
     */    
    public static void toggleFlight(Player player, boolean silent, boolean forced) {
    	if (player.getAllowFlight()) {
    		if (!silent)
    			if (forced) 
    				player.sendMessage(pluginPrefix + flightDeactivatedMsg + flightOffMsg);
    			else
    				player.sendMessage(pluginPrefix + flightOffMsg);
    		if (player.isFlying())
    			player.setFallDistance(-100000);
    		player.setAllowFlight(false);
    	} else {
    		if (!silent)
    			player.sendMessage(pluginPrefix + flightOnMsg);
    		player.setAllowFlight(true);
    	}    		
    }  
}
