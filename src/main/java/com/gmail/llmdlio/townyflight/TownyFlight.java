package com.gmail.llmdlio.townyflight;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.gmail.llmdlio.townyflight.listeners.PlayerEnterTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerFallListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerJoinListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLeaveTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerPVPListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerTeleportListener;
import com.gmail.llmdlio.townyflight.listeners.TownUnclaimListener;
import com.palmergames.bukkit.util.Version;

public class TownyFlight extends JavaPlugin {
	private static Version requiredTownyVersion = Version.fromString("0.97.2.15"); 
	
	private final PlayerEnterTownListener playerEnterListener = new PlayerEnterTownListener(this);
	private final PlayerJoinListener playerJoinListener = new PlayerJoinListener(this);
	private final PlayerLeaveTownListener playerLeaveListener = new PlayerLeaveTownListener(this);	
	private final PlayerPVPListener playerPVPListener = new PlayerPVPListener();
	private final TownUnclaimListener townUnclaimListener = new TownUnclaimListener(this);
	private final PlayerFallListener playerFallListener = new PlayerFallListener();
	private final PlayerTeleportListener playerTeleportListener = new PlayerTeleportListener();

	private TownyFlightConfig config = new TownyFlightConfig(this);
	private static TownyFlight plugin;
	private static TownyFlightAPI api = null;

	public void onEnable() {

		plugin = this;
		api = new TownyFlightAPI(this);

		if (!loadSettings()) {
			getLogger().severe("Config failed to load!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		Plugin test = getServer().getPluginManager().getPlugin("WarsForTowny");
		if (test != null)
			Settings.warsForTownyFound = true;

		test = getServer().getPluginManager().getPlugin("SiegeWar");
		if (test != null)
			Settings.siegeWarFound = true;

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

	public boolean loadSettings() {
		reloadConfig();
		return !Settings.loadSettings(config);
	}
	
	public void reloadConfig() {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		config.reload();
	}

	public void registerEvents() {
		final PluginManager pm = getServer().getPluginManager();
		if (Settings.autoEnableFlight)
			pm.registerEvents(playerEnterListener, this);
		pm.registerEvents(playerJoinListener, this);
		pm.registerEvents(playerLeaveListener, this);
		if (Settings.disableCombatPrevention)
			pm.registerEvents(playerPVPListener, this);
		pm.registerEvents(townUnclaimListener, this);
		pm.registerEvents(playerFallListener, this);
		pm.registerEvents(playerTeleportListener, this);
	}

	public void unregisterEvents() {
		HandlerList.unregisterAll(playerEnterListener);
		HandlerList.unregisterAll(playerJoinListener);
		HandlerList.unregisterAll(playerLeaveListener);
		HandlerList.unregisterAll(playerPVPListener);
		HandlerList.unregisterAll(townUnclaimListener);
		HandlerList.unregisterAll(playerFallListener);
		HandlerList.unregisterAll(playerTeleportListener);
	}

	public static TownyFlight getPlugin() {
		return plugin;
	}

	/**
	 * @return the api
	 */
	public static TownyFlightAPI getAPI() {
		return api;
	}

}
