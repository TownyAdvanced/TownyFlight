package com.gmail.llmdlio.townyflight;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.gmail.llmdlio.townyflight.listeners.PlayerEnterTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerFallListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerJoinListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLeaveTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLogOutListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerPVPListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerTeleportListener;
import com.gmail.llmdlio.townyflight.listeners.TownStatusScreenListener;
import com.gmail.llmdlio.townyflight.listeners.TownUnclaimListener;
import com.palmergames.bukkit.util.Version;

public class TownyFlight extends JavaPlugin {
	private static Version requiredTownyVersion = Version.fromString("0.98.2.0");
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
		pm.registerEvents(new PlayerLogOutListener(), this);
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
	}
}
