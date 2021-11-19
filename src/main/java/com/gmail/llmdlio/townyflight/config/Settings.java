package com.gmail.llmdlio.townyflight.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class Settings {

	public static Boolean autoEnableFlight;
	public static Boolean autoEnableSilent;
	public static Boolean disableCombatPrevention;
	public static Boolean disableDuringWar;
	public static Boolean showPermissionInMessage;
	public static Boolean warsForTownyFound = false;
	public static Boolean siegeWarFound = false;
	public static int flightDisableTimer;
	private static Map<String, String> lang = new HashMap<String,String>();

	public static boolean loadSettings(TownyFlightConfig config) {
		autoEnableFlight = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Flight").equalsIgnoreCase("true");
		autoEnableSilent = config.getConfig().getConfigurationSection("options").getString("auto_Enable_Silent").equalsIgnoreCase("true");
		disableDuringWar = config.getConfig().getConfigurationSection("options").getString("disable_During_Wartime").equalsIgnoreCase("true");
		disableCombatPrevention = config.getConfig().getConfigurationSection("options").getString("disable_Combat_Prevention").equalsIgnoreCase("true");
		showPermissionInMessage = config.getConfig().getConfigurationSection("options").getString("show_Permission_After_No_Permission_Message").equalsIgnoreCase("true");
		flightDisableTimer = Integer.valueOf(config.getConfig().getConfigurationSection("options").getString("flight_Disable_Timer"));
		// Load the language strings.
		loadStrings(config);
		return true;
	}

	public static void loadStrings(TownyFlightConfig config) {
		lang.clear();
		lang.put("pluginPrefix", ChatColor.translateAlternateColorCodes('&', config.getConfig().getString("pluginPrefix")));
		lang.put("flightOnMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightOnMsg")));
		lang.put("flightOffMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightOffMsg")));
		lang.put("noTownMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("noTownMsg")));
		lang.put("notInTownMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("notInTownMsg")));
		lang.put("flightDeactivatedMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedMsg")));
		lang.put("flightDeactivatedPVPMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedPVPMsg")));
		lang.put("flightDeactivatedConsoleMsg", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("flightDeactivatedConsoleMsg")));
		lang.put("noPermission", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("noPermission")));
		lang.put("missingNode", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("missingNode")));
		lang.put("notDuringWar", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("notDuringWar")));
		lang.put("returnToAllowedArea", ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString("returnToAllowedArea")));
	}

	public static String getLangString(String languageString) {
		return lang.get(languageString);
	}
}
