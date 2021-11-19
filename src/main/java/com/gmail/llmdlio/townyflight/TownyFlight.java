package com.gmail.llmdlio.townyflight;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.llmdlio.townyflight.config.Settings;
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
	private TownyFlightConfig config = new TownyFlightConfig(this);
	private static TownyFlight plugin;
	private static TownyFlightAPI api = null;

	public void onEnable() {

		plugin = this;
		api = new TownyFlightAPI(this);

		if (!loadSettings()) {
			getLogger().severe("Config failed to load!");
			return;
		}

		if (!checkTownyVersion())
			return;

		checkWarPlugins();
		registerEvents();
		registerCommands();
		getLogger().info(this.getDescription().getFullName() + " by LlmDl Enabled.");
	}

	private boolean checkTownyVersion() {
		Plugin towny = getServer().getPluginManager().getPlugin("Towny");
		if (!townyVersionCheck(towny.getDescription().getVersion())) {
			getLogger().severe("Towny version does not meet required version: " + requiredTownyVersion.toString());
			return false;
		}

		getLogger().info("Towny version " + towny.getDescription().getVersion() + " found.");
		return true;
	}

	public void onDisable() {
		unregisterEvents();
		getLogger().info("TownyFlight Disabled.");
	}

	private void registerCommands() {
		getCommand("tfly").setExecutor(new TownyFlightCommand(this));
	}

	private void checkWarPlugins() {
		Plugin test = getServer().getPluginManager().getPlugin("WarsForTowny");
		if (test != null)
			Settings.warsForTownyFound = true;

		test = getServer().getPluginManager().getPlugin("SiegeWar");
		if (test != null)
			Settings.siegeWarFound = true;
	}

	private boolean townyVersionCheck(String version) {
		return Version.fromString(version).compareTo(requiredTownyVersion) >= 0;
	}

	protected boolean loadSettings() {
		reloadConfig();
		return Settings.loadSettings(config);
	}

	public void reloadConfig() {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		config.reload();
	}

	void registerEvents() {
		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerJoinListener(this), this);
		pm.registerEvents(new PlayerLeaveTownListener(this), this);
		pm.registerEvents(new TownUnclaimListener(this), this);
		pm.registerEvents(new PlayerFallListener(), this);
		pm.registerEvents(new PlayerTeleportListener(), this);
		if (Settings.autoEnableFlight)
			pm.registerEvents(new PlayerEnterTownListener(this), this);
		if (Settings.disableCombatPrevention)
			pm.registerEvents(new PlayerPVPListener(), this);
	}

	protected void unregisterEvents() {
		HandlerList.unregisterAll(this);
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

}
