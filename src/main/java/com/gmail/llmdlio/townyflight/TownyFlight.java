package com.gmail.llmdlio.townyflight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.gmail.llmdlio.townyflight.listeners.PlayerEnterTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerFallListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerJoinListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLeaveTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerPVPListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerTeleportListener;
import com.gmail.llmdlio.townyflight.listeners.TownUnclaimListener;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.util.Version;

public class TownyFlight extends JavaPlugin {
	private static Version requiredTownyVersion = Version.fromString("0.96.5.5"); 
	
	private final PlayerEnterTownListener playerEnterListener = new PlayerEnterTownListener(this);
	private final PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
	private final PlayerLeaveTownListener playerLeaveListener = new PlayerLeaveTownListener(this);	
	private final PlayerPVPListener playerPVPListener = new PlayerPVPListener();
	private final TownUnclaimListener townUnclaimListener = new TownUnclaimListener();
	private final PlayerFallListener playerFallListener = new PlayerFallListener();
	private final PlayerTeleportListener playerTeleportListener = new PlayerTeleportListener();

	public static String pluginPrefix;
	private static String flightOnMsg;
	private static String flightOffMsg;
	private static String noTownMsg;
	private static String notInTownMsg;
	private static String flightDeactivatedMsg;
	private static String flightDeactivatedPVPMsg;
	private static String flightDeactivatedConsoleMsg;
	private static String noPermission;
	private static String notDuringWar;
	public static String returnToAllowedArea;

	public static Boolean autoEnableFlight;
	public static Boolean autoEnableSilent;
	public static Boolean disableCombatPrevention;
	private static Boolean disableDuringWar;
	private static Boolean showPermissionInMessage;
	private static Boolean warsForTownyFound = false;
	public static int flightDisableTimer;
	
	public static List<Player> flyingPlayers = new ArrayList<>();

	private TownyFlightConfig config = new TownyFlightConfig(this);
	private static TownyFlight plugin;

	public void onEnable() {

		plugin = this;
    	reloadConfig();

    	if (!loadSettings()) {
    		getLogger().severe("Config failed to load!");
    		this.getServer().getPluginManager().disablePlugin(this);
    		return;
    	}
    	
    	Plugin test = getServer().getPluginManager().getPlugin("WarsForTowny");
		if (test != null)
			warsForTownyFound = true;

		Plugin towny = getServer().getPluginManager().getPlugin("Towny");
		if (!townyVersionCheck(towny.getDescription().getVersion())) {
			getLogger().severe("Towny version does not meet required version: " + requiredTownyVersion.toString());
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Towny version " + towny.getDescription().getVersion() + " found.");
		}

    	registerEvents();
	    getLogger().info(this.getDescription().getFullName() + " by LlmDl Enabled.");
	}

	private boolean townyVersionCheck(String version) {
		Version ver = Version.fromString(version);
		
		return ver.compareTo(requiredTownyVersion) >= 0;
    }

    public void onDisable() {
    	getLogger().info("TownyFlight Disabled.");
    }

    public void reloadConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        config.reload();
    }

    private boolean loadSettings() {
		pluginPrefix = ChatColor.translateAlternateColorCodes('&', config.getConfig().getString("pluginPrefix"));

		// Language Strings
		flightOnMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightOnMsg"));
		flightOffMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightOffMsg"));
		noTownMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("noTownMsg"));
		notInTownMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("notInTownMsg"));
		flightDeactivatedMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedMsg"));
		flightDeactivatedPVPMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedPVPMsg"));
		flightDeactivatedConsoleMsg = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedConsoleMsg"));
		noPermission = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("noPermission"));
		notDuringWar = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("notDuringWar"));
		returnToAllowedArea = ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("returnToAllowedArea"));

		// Options
		autoEnableFlight = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Flight").equalsIgnoreCase("true");
		autoEnableSilent = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Silent").equalsIgnoreCase("true");
		disableDuringWar = config.getConfig().getConfigurationSection("options").getString("disable_During_Wartime").equalsIgnoreCase("true");
		disableCombatPrevention = config.getConfig().getConfigurationSection("options").getString("disable_Combat_Prevention").equalsIgnoreCase("true");
		showPermissionInMessage = config.getConfig().getConfigurationSection("options").getString("show_Permission_After_No_Permission_Message").equalsIgnoreCase("true");
		flightDisableTimer = Integer.valueOf(config.getConfig().getConfigurationSection("options").getString("flight_Disable_Timer"));

		return true;
	}

    private void registerEvents(){
    	final PluginManager pluginManager = getServer().getPluginManager();
    	if (autoEnableFlight)
    		pluginManager.registerEvents(playerEnterListener, this);
    	pluginManager.registerEvents(playerJoinListener, this);
    	pluginManager.registerEvents(playerLeaveListener, this);
    	if (disableCombatPrevention)
    		pluginManager.registerEvents(playerPVPListener, this);
    	pluginManager.registerEvents(townUnclaimListener, this);
    	pluginManager.registerEvents(playerFallListener, this);
    	pluginManager.registerEvents(playerTeleportListener, this);
    }
    
    private void unregisterEvents() {
    	HandlerList.unregisterAll(playerEnterListener);
    	HandlerList.unregisterAll(playerJoinListener);
    	HandlerList.unregisterAll(playerLeaveListener);
    	HandlerList.unregisterAll(playerPVPListener);
    	HandlerList.unregisterAll(townUnclaimListener);
    	HandlerList.unregisterAll(playerFallListener);
    	HandlerList.unregisterAll(playerTeleportListener);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tfly")) {

			if (sender instanceof ConsoleCommandSender) {

				if (args[0].equalsIgnoreCase("reload")) {
					// Reload the plugin.
					return reloadPlugin(sender);
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					return toggleFlightOnOther(sender, args[0]);
				}
			}

		 	if (sender instanceof Player) {

		 	    if (args.length != 0) {
		 	    	// We have more than just /tfly
		 	    	
		 	    	if (args[0].equalsIgnoreCase("reload")) {
			 	    	// We have /tfly reload, test for permission node.
		 	    		if (!sender.hasPermission("townyflight.command.tfly.reload")) {
							sender.sendMessage(pluginPrefix + ChatColor.RED + noPermission + ((showPermissionInMessage) ? "townyflight.command.tfly.reload" : ""));
							return true;
						}
		 	    		// Reload the plugin.
						return reloadPlugin(sender);
		 	    	} else {
		 	    		//  It's not any other subcommand of /tfly so handle removing flight via /tfly {name}		 	    		
		 	    		if (!sender.hasPermission("townyflight.command.tfly.other")) {
							sender.sendMessage(pluginPrefix + ChatColor.RED + noPermission + ((showPermissionInMessage) ? "townyflight.command.tfly.other" : ""));
							return true;
		 	    		}
		 	    		// Send the name off to attempt to remove their flight.
		 	    		return toggleFlightOnOther(sender, args[0]);
		 	    	}
		 	    }
		 	    
		 	    // We have only /tfly
                if (!canFly((Player) sender, false))
                    return true;
                toggleFlight((Player) sender, false, false, "");
                return true;
			}
		}
		return false;
    }
    
    private boolean reloadPlugin(CommandSender sender) {
		config.reload();
    	loadSettings();
    	unregisterEvents();
    	registerEvents();
		sender.sendMessage(pluginPrefix + "Config.yml reloaded");
		return true;
    }
    
    private boolean toggleFlightOnOther(CommandSender sender, String name) {

		Player player = Bukkit.getPlayerExact(name);
		if (player != null && player.isOnline())
	    	if (!player.getAllowFlight()) {
	    		sender.sendMessage(pluginPrefix + "Player " + name + " is already unable to fly. Could not remove flight.");
	    	} else {
		    	toggleFlight(player.getPlayer(), false, true, "console");
				sender.sendMessage(pluginPrefix + "Flight removed from " + name + ".");
	    	}
		else
			sender.sendMessage(pluginPrefix + "Player " + name + " not found, or is offline. Could not remove flight.");

		return true;
    }

	/** 
     * Returns true if a player can fly according to TownyFlight's rules.
     * 
     * @param player
     * @param silent - show messages to player.
     **/
    public static boolean canFly(Player player, boolean silent) {
    	if (player.hasPermission("townyflight.bypass") || player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE))
    		return true;
    	if (!player.hasPermission("townyflight.command.tfly")) {
    		if (!silent) player.sendMessage(pluginPrefix + ChatColor.RED + noPermission + ((showPermissionInMessage) ? "townyflight.command.tfly" : ""));
        	return false;
        }
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null)
			return false;

		if (disableDuringWar && (TownyAPI.getInstance().isWarTime() || warsForTowny(resident))) {
			if (!silent) player.sendMessage(pluginPrefix + notDuringWar);
			return false;
		}
		if (!resident.hasTown()) {
			if (!silent) player.sendMessage(pluginPrefix + noTownMsg);
			return false;
		}
		if (!allowedLocation(player)) {
			if (!silent) player.sendMessage(pluginPrefix + notInTownMsg);
			return false;
		}
		return true;
    }

    private static boolean warsForTowny(Resident resident) {
    	if (!warsForTownyFound)
    		return false;
    	if (!resident.hasTown())
    		return false;
    	try {
			if (!resident.getTown().hasNation())
				return false;
			if (com.aurgiyalgo.WarsForTowny.WarManager.getWarForNation(resident.getTown().getNation()) != null)
				return true;
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
    	return false;
	}

	/**
     * Returns true if a player is allowed to fly at their current location.
     * Blocks wilderness flight, then check if they are in their own town and if not,
     * whether they have the alliedtowns permission and if they are in an allied area.
     * 
     * @param player
     * @return true if player is allowed to be flying at their present location.
     */
    private static boolean allowedLocation(Player player) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        if (resident == null)
            return false;
        
		if (TownyAPI.getInstance().isWilderness(player.getLocation()))
			return false;
		
		if (player.hasPermission("townyflight.alltowns"))
			return true;

		try {
			Town town = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
			if (resident.getTown() == town)
				return true;
			if (player.hasPermission("townyflight.alliedtowns"))
				return CombatUtil.isAlly(town, resident.getTown());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
     * If flight is on, turn it off and vice versa
     * 
     * @param player
     * @param silent - show messages to player
     * @param forced - whether this is a forced deactivation or not
     * @param cause - cause of disabling flight ("", "pvp", "console")
     */
    @SuppressWarnings("deprecation")
    private static void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
    	if (player.getAllowFlight()) {
    		if (!silent) {
    			if (forced) {
    				String reason = flightDeactivatedMsg;
    				if (cause == "pvp") 
    					reason = flightDeactivatedPVPMsg;
    				if (cause == "console") 
    					reason = flightDeactivatedConsoleMsg;
    				player.sendMessage(pluginPrefix + reason + flightOffMsg);
    			} else {
    				player.sendMessage(pluginPrefix + flightOffMsg);
    			}
    		}
    		if (player.isFlying()) {
    			// As of 1.15 the below line does not seem to be reliable.
        		player.setFallDistance(-100000);
        		// As of 1.15 the below is required.
        		if (!player.isOnGround()) {
        			flyingPlayers.add(player);
        			new BukkitRunnable() {
    					@Override
    					public void run() {
    						removeFallProtection(player);
    					}
    				}.runTaskLater(plugin, 100);
        		}
    		}
    		player.setAllowFlight(false);
    	} else {
    		if (!silent) 
    			player.sendMessage(pluginPrefix + flightOnMsg);
    		player.setAllowFlight(true);
    	}
    }
    
    /**
     * Turn off flight.
     * 
     * @param player
     * @param silent - show messages to player
     * @param forced - whether this is a forced deactivation or not
     * @param cause - cause of disabling flight ("", "pvp", "console")
     */
    @SuppressWarnings("deprecation")
    public static void removeFlight(Player player, boolean silent, boolean forced, String cause) {
        if (!silent) {
            if (forced) {
                String reason = flightDeactivatedMsg;
                if (cause == "pvp") 
                    reason = flightDeactivatedPVPMsg;
                if (cause == "console") 
                    reason = flightDeactivatedConsoleMsg;
                player.sendMessage(pluginPrefix + reason + flightOffMsg);
            } else {
                player.sendMessage(pluginPrefix + flightOffMsg);
            }
        }
        if (player.isFlying()) {
            // As of 1.15 the below line does not seem to be reliable.
            player.setFallDistance(-100000);
            // As of 1.15 the below is required.
            if (!player.isOnGround()) {
                flyingPlayers.add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        removeFallProtection(player);
                    }
                }.runTaskLater(plugin, 100);
            }
        }
        player.setAllowFlight(false);
    }
    
    /**
     * Turn flight on.
     * 
     * @param player
     * @param silent - show messages to player
     */
    public static void addFlight(Player player, boolean silent) {
        if (!silent) 
            player.sendMessage(pluginPrefix + flightOnMsg);
        player.setAllowFlight(true);
    }
    
	private static void removeFallProtection(Player player) {
		if (flyingPlayers.contains(player))
			flyingPlayers.remove(player);
	}
}
