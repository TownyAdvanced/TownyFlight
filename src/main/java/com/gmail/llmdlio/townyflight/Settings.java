package com.gmail.llmdlio.townyflight;

import org.bukkit.ChatColor;

import com.gmail.llmdlio.townyflight.config.TownyFlightConfig;

public class Settings {

	static String pluginPrefix;
	static String flightOnMsg;
	static String flightOffMsg;
	static String noTownMsg;
	static String notInTownMsg;
	static String flightDeactivatedMsg;
	static String flightDeactivatedPVPMsg;
	static String flightDeactivatedConsoleMsg;
	static String noPermission;
	static String notDuringWar;
	public static String returnToAllowedArea;

	public static Boolean autoEnableFlight;
	public static Boolean autoEnableSilent;
	static Boolean disableCombatPrevention;
	static Boolean disableDuringWar;
	static Boolean showPermissionInMessage;
	static Boolean warsForTownyFound = false;
	static Boolean siegeWarFound = false;
	public static int flightDisableTimer;
	
	public Settings() {
		// TODO Auto-generated constructor stub
	}

    public static boolean loadSettings(TownyFlightConfig config) {
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
}
