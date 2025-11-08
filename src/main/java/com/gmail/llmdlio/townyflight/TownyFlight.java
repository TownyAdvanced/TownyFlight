package com.gmail.llmdlio.townyflight;

import com.palmergames.bukkit.towny.scheduling.TaskScheduler;
import com.palmergames.bukkit.towny.scheduling.impl.BukkitTaskScheduler;
import com.palmergames.bukkit.towny.scheduling.impl.FoliaTaskScheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.llmdlio.townyflight.command.TownToggleFlightCommandAddon;
import com.gmail.llmdlio.townyflight.command.TownyFlightCommand;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;
import com.gmail.llmdlio.townyflight.integrations.TownyFlightPlaceholderExpansion;
import com.gmail.llmdlio.townyflight.listeners.PlayerEnterTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerFallListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerJoinListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLeaveTownListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerLogOutListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerPVPListener;
import com.gmail.llmdlio.townyflight.listeners.PlayerTeleportListener;
import com.gmail.llmdlio.townyflight.listeners.TownRemoveResidentListener;
import com.gmail.llmdlio.townyflight.listeners.TownStatusScreenListener;
import com.gmail.llmdlio.townyflight.listeners.TownUnclaimListener;
import com.gmail.llmdlio.townyflight.tasks.TaskHandler;
import com.gmail.llmdlio.townyflight.tasks.TempFlightTask;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.palmergames.bukkit.util.Version;

public class TownyFlight extends JavaPlugin {
	private static final Version requiredTownyVersion = Version.fromString("0.101.2.5");
	private TownyFlightConfig config = new TownyFlightConfig(this);
	private static TownyFlight plugin;
	private static TownyFlightAPI api;
	private TownyFlightPlaceholderExpansion papiExpansion = null;
	private final TaskScheduler scheduler;

	public TownyFlight() {
		plugin = this;
		this.scheduler = isFoliaClassPresent() ? new FoliaTaskScheduler(this) : new BukkitTaskScheduler(this);
	}

	public void onEnable() {
		api = new TownyFlightAPI(this);
		String townyVersion = getServer().getPluginManager().getPlugin("Towny").getPluginMeta().getVersion();

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
		checkIntegrations();
		registerEvents();
		registerCommands();
		getLogger().info("Towny version " + townyVersion + " found.");
		getLogger().info(this.getPluginMeta().getDisplayName() + " by LlmDl Enabled.");
		
		cycleTimerTasksOn();
		reGrantTempFlightToOnlinePlayer();
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

	public boolean loadSettings() {
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
		Settings.siegeWarFound = getServer().getPluginManager().getPlugin("SiegeWar") != null;
	}


	private void checkIntegrations() {
		Plugin test;
		test = getServer().getPluginManager().getPlugin("PlaceholderAPI");
		if (test != null) {
			papiExpansion = new TownyFlightPlaceholderExpansion(this);
			papiExpansion.register();
		}
	}

	public void registerEvents() {
		final PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new PlayerJoinListener(this), this);
		pm.registerEvents(new PlayerLogOutListener(), this);
		pm.registerEvents(new PlayerLeaveTownListener(this), this);
		pm.registerEvents(new TownRemoveResidentListener(this), this);
		pm.registerEvents(new TownUnclaimListener(this), this);
		pm.registerEvents(new PlayerFallListener(), this);
		pm.registerEvents(new PlayerTeleportListener(), this);
		pm.registerEvents(new TownStatusScreenListener(), this);
		pm.registerEvents(new PlayerEnterTownListener(this), this);

		if (Settings.disableCombatPrevention)
			pm.registerEvents(new PlayerPVPListener(), this);
	}

	public void unregisterEvents() {
		HandlerList.unregisterAll(this);
	}

	private void registerCommands() {
		getCommand("tfly").setExecutor(new TownyFlightCommand(this));
		new TownToggleFlightCommandAddon();
	}

	private void cycleTimerTasksOn() {
		cycleTimerTasksOff();
		TaskHandler.toggleTempFlightTask(true);
	}

	private void cycleTimerTasksOff() {
		TaskHandler.toggleTempFlightTask(false);
	}

	private void reGrantTempFlightToOnlinePlayer() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			long seconds = MetaData.getSeconds(player.getUniqueId());
			if (seconds > 0L)
				TempFlightTask.addPlayerTempFlightSeconds(player.getUniqueId(), seconds);
		}
	}

	public TaskScheduler getScheduler() {
		return this.scheduler;
	}

	private static boolean isFoliaClassPresent() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
