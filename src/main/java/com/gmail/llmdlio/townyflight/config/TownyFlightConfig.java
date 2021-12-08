package com.gmail.llmdlio.townyflight.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;

import com.gmail.llmdlio.townyflight.TownyFlight;

public class TownyFlightConfig {
	private TownyFlight plugin;
	private CommentedYamlConfiguration config;

	public TownyFlightConfig(TownyFlight plugin) {
		this.plugin = plugin;
	}

	public boolean reload() {
		return loadConfig();
	}

	// Method to load TownyFlight\config.yml
	private boolean loadConfig() {
		File f = new File(plugin.getDataFolder(), "config.yml");

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = new CommentedYamlConfiguration();

		try {
			config.load(f);
		} catch (IOException | InvalidConfigurationException e) {
			plugin.getLogger().severe("Config.yml Error: " + e.getMessage());
			return false;
		}

		addComment("Version", "# TownyFlight by LlmDl.");
		addDefault("Version", plugin.getDescription().getVersion());
		addComment("pluginPrefix", "# Prefix to messages seen in game.");
		addDefault("pluginPrefix", "&8[&3TownyFlight&8] ");

		addComment("language", "", "",
				"####################",
				"# Language Strings #",
				"####################", "");
		addComment("language.flightOnMsg", "# Message shown when flight activated.");
		addDefault("language.flightOnMsg", "Flight Activated. ");
		addComment("language.flightOffMsg", "# Message shown when flight de-activated.");
		addDefault("language.flightOffMsg", "Flight De-activated. ");
		addComment("language.noTownMsg", "# Message shown when player lacks a town. ");
		addDefault("language.noTownMsg", "Flight cannot be activated, you don't belong to a town. ");
		addComment("language.notInTownMsg", "# Message shown when flight cannot be turned on.");
		addDefault("language.notInTownMsg", "Flight cannot be activated, return to your town and try again. ");
		addComment("language.flightDeactivatedMsg", "# Message shown when a player has flight taken away.");
		addDefault("language.flightDeactivatedMsg", "Left town boundaries. ");
		addComment("language.flightDeactivatedPVPMsg","# Message shown when a player has flight taken away because of PVP.");
		addDefault("language.flightDeactivatedPVPMsg", "Entering PVP combat. ");
		addComment("language.flightDeactivatedConsoleMsg", "# Message shown when a player has flight taken away by console.");
		addDefault("language.flightDeactivatedConsoleMsg", "Flight priviledges removed. ");
		addComment("language.noPermission", "# Message shown when a player lacks a permission node.");
		addDefault("language.noPermission", "You do not have permission for this command%s. ");
		addComment("language.missingNode", "# Message attached to noPermission when options.show_Permission_After_No_Permission_Message is true");
		addDefault("language.missingNode", ", missing %s");
		addComment("language.notDuringWar", "# Message shown when war is active and flight is disallowed.");
		addDefault("language.notDuringWar", "You cannot use flight while Towny war is active. ");
		addComment("language.returnToAllowedArea", "# Message telling a player to return to an allowed flight area.");
		addDefault("language.returnToAllowedArea", "You have %s seconds to return to an allowed flight area. ");
		addComment("language.noTownFound", "# Message when a town cannot be found by the name.");
		addDefault("language.noTownFound", "TownyFlight cannot find a town by the name %s. ");
		addComment("language.townWideFlight", "# Message when a town has free flight enabled or disabled.");
		addDefault("language.townWideFlight", "Free flight has been %s in %s. ");
		addComment("language.disabled", "# The word disabled.");
		addDefault("language.disabled", "disabled");
		addComment("language.enabled", "# The world enabled.");
		addDefault("language.enabled", "enabled");
		addComment("language.statusScreenComponent", "# The component shown on towns' status screens when they have free flight enabled.");
		addDefault("language.statusScreenComponent", "Free Flight");
		addComment("language.statusScreenComponentHover", "# The hover text shown on the free flight status screen component.");
		addDefault("language.statusScreenComponentHover", "Flight enabled for everyone within this town's borders.");

		addComment("options", "", "", "", 
				"#################", 
				"#    Options    #", 
				"#################", "");
		addComment("options.auto_Enable_Flight",
				"# If set to true, players entering their town will have flight auto-enabled.",
				"# When set to true, the plugin will use slightly more resources due to the EnterTown listener.");
		addDefault("options.auto_Enable_Flight", "false");
		addComment("options.auto_Enable_Silent", "# If set to true, players entering their town will have flight auto-enabled without being notified in chat.");
		addDefault("options.auto_Enable_Silent", "false");
		addComment("options.disable_During_Wartime", "# If set to false, players can still fly in their town while war is active.");
		addDefault("options.disable_During_Wartime", "true");
		addComment("options.disable_Combat_Prevention", "# If set to false, TownyFlight will not prevent combat of flying people.");
		addDefault("options.disable_Combat_Prevention", "false");
		addComment("options.show_Permission_After_No_Permission_Message", "# If set to false, the language.noPermission message will not display the permission node.");
		addDefault("options.show_Permission_After_No_Permission_Message", "true");
		addComment("options.flight_Disable_Timer", "# Number of seconds after leaving an allowed flight area before flight is taken away.", "# Set to 0 to take flight away immediately.");
		addDefault("options.flight_Disable_Timer", "3");

		// Write back config
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	public CommentedYamlConfiguration getConfig() {
		return config;
	}

	private boolean hasPath(String path) {
		return config.isSet(path);
	}

	private void addComment(String path, String... comment) {
		config.addComment(path, comment);
	}

	private void addDefault(String path, Object defaultValue) {
		if (path.equals("Version"))
			config.set(path, plugin.getDescription().getVersion());
		if (!hasPath(path))
			config.set(path, defaultValue);
	}
}
