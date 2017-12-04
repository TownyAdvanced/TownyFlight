package com.gmail.llmdlio.townyflight;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class TownyFlight extends JavaPlugin implements Listener {

	private static String pluginPrefix;
	private static String flightOnMsg;
	private static String flightOffMsg;
	private static String noTownMsg;
	private static String notInTownMsg;
	private static String flightDeactivatedMsg;	
	
	private TownyFlightConfig config = new TownyFlightConfig(this);
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
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
		flightOnMsg = config.getConfig().getString("flightOnMsg");
		flightOffMsg = config.getConfig().getString("flightOffMsg");
		noTownMsg = config.getConfig().getString("noTownMsg");
		notInTownMsg = config.getConfig().getString("notInTownMsg");
		flightDeactivatedMsg = config.getConfig().getString("flightDeactivatedMsg");
		return true;		
	}
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tfly")) {
			if (args.length == 0) {
				
				if (!(sender instanceof Player))
					return false;
				
                if (!sender.hasPermission("townyflight.command.tfly")) {
                	sender.sendMessage(pluginPrefix + ChatColor.RED + "Insufficient Permissions.");
                	return false;
                }

                Player player = (Player) sender;
                Resident resident = null;
                try {
					resident= TownyUniverse.getDataSource().getResident(player.getName());
				} catch (NotRegisteredException e1) {
				}
                if (!resident.hasTown()) {
                	sender.sendMessage(pluginPrefix + noTownMsg);
                	return false;
                }                
                
                if (TownyUniverse.isWilderness(player.getLocation().getBlock())) {
                	sender.sendMessage(pluginPrefix + notInTownMsg);
                	return false;
                }
                
                Town town = null;
                try {
					town = TownyUniverse.getTownBlock(player.getLocation()).getTown();
				} catch (NotRegisteredException e) {
				}
                
            	try {
					if (!resident.getTown().equals(town)) {
						sender.sendMessage(pluginPrefix + notInTownMsg);
						return false;
					}
				} catch (NotRegisteredException e) {
				}
                toggleFlight(player);
                return true;
	        }
			
			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("townyflight.command.tfly.reload")) {
					sender.sendMessage(pluginPrefix + ChatColor.RED + "Insufficient Permissions.");
					return false;
				}
				config.reload();
				LoadSettings();
				sender.sendMessage(pluginPrefix + "Config.yml reloaded");
				return true;
			}
		}
		return false;
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerLeftTownEvent (PlayerLeaveTownEvent event) {    	
    	Player player = event.getPlayer();
    	if (player.isFlying()){    		
    		toggleFlight(player);
    		player.sendMessage(pluginPrefix + flightDeactivatedMsg);
    	}
    }
    
    private void toggleFlight(Player player) {
    	if (player.getAllowFlight()) {
    		player.sendMessage(pluginPrefix + flightOffMsg);
    		if (player.isFlying())
    			player.setFallDistance(-100000);
    		player.setAllowFlight(false);
    	} else {
    		player.sendMessage(pluginPrefix + flightOnMsg);
    		player.setAllowFlight(true);
    	}    		
    }  
}
