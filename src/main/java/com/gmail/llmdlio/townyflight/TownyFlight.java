package com.gmail.llmdlio.townyflight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
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
import com.gmail.llmdlio.townyflight.listeners.TownUnclaimListener;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;

public class TownyFlight extends JavaPlugin {
	
	private final PlayerEnterTownListener playerEnterListener = new PlayerEnterTownListener(this);
	private final PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
	private final PlayerLeaveTownListener playerLeaveListener = new PlayerLeaveTownListener(this);	
	private final PlayerPVPListener playerPVPListener = new PlayerPVPListener();
	private final TownUnclaimListener townUnclaimListener = new TownUnclaimListener();
	private final PlayerFallListener playerFallListener = new PlayerFallListener();

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
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			getLogger().info("Towny version " + towny.getDescription().getVersion() + " found.");
		}

    	registerEvents();
	    getLogger().info(this.getDescription().getFullName() + " by LlmDl Enabled.");
	}

	private boolean townyVersionCheck(String version) {
		// This was not terrible useful or well-made so for now we are disabling the version checking.
		return true; 
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
    	HandlerList.unregisterAll(playerEnterListener);
    	if (autoEnableFlight)
    		pluginManager.registerEvents(playerEnterListener, this);
    	pluginManager.registerEvents(playerJoinListener, this);
    	pluginManager.registerEvents(playerLeaveListener, this);
    	if (disableCombatPrevention)
    		pluginManager.registerEvents(playerPVPListener, this);
    	pluginManager.registerEvents(townUnclaimListener, this);
    	pluginManager.registerEvents(playerFallListener, this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tfly")) {

			if (sender instanceof ConsoleCommandSender) {

				if (args[0].equalsIgnoreCase("reload")) {
					config.reload();
			    	loadSettings();
			    	registerEvents();
					sender.sendMessage(pluginPrefix + "Config.yml reloaded.");
					return true;
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					@SuppressWarnings("deprecation")
					OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
					if (player.isOnline()) {
				    	if (!player.getPlayer().getAllowFlight()) {
				    		sender.sendMessage(pluginPrefix + "Player " + args[0] + " is already unable to fly. Could not remove flight.");
							return true;
				    	}
				    	toggleFlight(player.getPlayer(), false, true, "console");
						sender.sendMessage(pluginPrefix + "Flight removed from " + args[0] + ".");
						return true;
					} else {
						sender.sendMessage(pluginPrefix + "Player " + args[0] + " not found, or is offline. Could not remove flight.");
						return true;
					}
				}
			}

		 	if (sender instanceof Player) {

		 	    if (args.length != 0 && args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("townyflight.command.tfly.reload")) {
						sender.sendMessage(pluginPrefix + ChatColor.RED + noPermission + ((showPermissionInMessage) ? "townyflight.command.tfly.reload" : ""));
						return true;
					}
					config.reload();
			    	loadSettings();
			    	registerEvents();
					sender.sendMessage(pluginPrefix + "Config.yml reloaded");
					return true;
				}
		 	    
                if (!canFly((Player) sender, false))
                    return true;
                toggleFlight((Player) sender, false, false, "");
                return true;
			}
		}
		return false;
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
		Resident resident = null;
		try {
			resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			// Sometimes when a player joins for the first time, there can be a canFly test run before Towny has 
			// the chance to save the player properly.
			return false;
		}
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
    	Resident resident = null;
		try {
			resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
		} catch (NotRegisteredException ignored) {
		}
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
    public static void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
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
    
	private static void removeFallProtection(Player player) {
		if (flyingPlayers.contains(player))
			flyingPlayers.remove(player);
	}
}
