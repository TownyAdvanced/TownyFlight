package com.olziedev.terraeflight;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.olziedev.terraeflight.config.Settings;
import com.olziedev.terraeflight.config.TownyFlightConfig;
import com.olziedev.terraeflight.listeners.PlayerEnterTownListener;
import com.olziedev.terraeflight.listeners.PlayerFallListener;
import com.olziedev.terraeflight.listeners.PlayerJoinListener;
import com.olziedev.terraeflight.listeners.PlayerLeaveTownListener;
import com.olziedev.terraeflight.listeners.PlayerPVPListener;
import com.olziedev.terraeflight.listeners.PlayerTeleportListener;
import com.olziedev.terraeflight.listeners.TownStatusScreenListener;
import com.olziedev.terraeflight.listeners.TownUnclaimListener;
import com.palmergames.bukkit.util.Version;

public class TownyFlight extends JavaPlugin {
	private static final Version requiredTownyVersion = Version.fromString("0.97.5.0");
	private TownyFlightConfig config = new TownyFlightConfig(this);
	private static TownyFlight plugin;
	private static TownyFlightAPI api;
	final PluginManager pm = getServer().getPluginManager();
	
	public void onEnable() {

		plugin = this;
		api = new TownyFlightAPI(this);
		String townyVersion = pm.getPlugin("Towny").getDescription().getVersion();

		if (!loadSettings()) {
			getLogger().severe("Config failed to load!");
			disable();
			return;
		}

		if (!townyVersionCheck(townyVersion)) {
			getLogger().severe("Towny version does not meet required version: " + requiredTownyVersion.toString());
			disable();
			return;
		}

		checkWarPlugins();
		registerEvents();
		registerCommands();
		getLogger().info("Towny version " + townyVersion + " found.");
		getLogger().info(this.getDescription().getFullName() + " by LlmDl Enabled.");
	}

	public static TownyFlight getPlugin() {
		return plugin;
	}

	/**
	 * @return the API.
	 */
	public static TownyFlightAPI getAPI() {
		return api;
	}

	private void disable() {
		unregisterEvents();
		getLogger().severe("TownyFlight Disabled.");
	}

	protected boolean loadSettings() {
		return loadConfig() && Settings.loadSettings(config);
	}

	private boolean loadConfig() {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		return config.reload();
	}

	private boolean townyVersionCheck(String version) {
		return Version.fromString(version).compareTo(requiredTownyVersion) >= 0;
	}

	private void checkWarPlugins() {
		Settings.siegeWarFound = pm.getPlugin("SiegeWar") != null;
	}

	protected void registerEvents() {
		pm.registerEvents(new PlayerJoinListener(), this);
		pm.registerEvents(new PlayerLeaveTownListener(), this);
		pm.registerEvents(new TownUnclaimListener(), this);
		pm.registerEvents(new PlayerFallListener(), this);
		pm.registerEvents(new PlayerTeleportListener(), this);
		pm.registerEvents(new TownStatusScreenListener(), this);
		if (Settings.autoEnableFlight) pm.registerEvents(new PlayerEnterTownListener(), this);
		if (Settings.disableCombatPrevention) pm.registerEvents(new PlayerPVPListener(), this);
	}

	protected void unregisterEvents() {
		HandlerList.unregisterAll(this);
	}

	private void registerCommands() {
		getCommand("tfly").setExecutor(new TownyFlightCommand(this));
		getCommand("nfly").setExecutor(new NationFlightCommand());
	}
}
