package com.gmail.llmdlio.townyflight.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class Settings {

	private static TownyFlightConfig config;
	public static Boolean autoEnableFlight;
	public static Boolean autoEnableSilent;
	public static Boolean disableCombatPrevention;
	public static Boolean disableDuringWar;
	public static Boolean showPermissionInMessage;
	public static Boolean warsForTownyFound;
	public static Boolean siegeWarFound;
	public static int flightDisableTimer;
	private static Map<String, String> lang = new HashMap<String,String>();

	public static boolean loadSettings(TownyFlightConfig _config) {
		config = _config;
		loadOptions();
		loadStrings();
		return true;
	}

	private static void loadOptions() {
		autoEnableFlight = Boolean.valueOf(getOption("auto_Enable_Flight"));
		autoEnableSilent = Boolean.valueOf(getOption("auto_Enable_Silent"));
		disableDuringWar = Boolean.valueOf(getOption("disable_During_Wartime"));
		disableCombatPrevention = Boolean.valueOf(getOption("disable_Combat_Prevention"));
		showPermissionInMessage = Boolean.valueOf(getOption("show_Permission_After_No_Permission_Message"));
		flightDisableTimer = Integer.valueOf(getOption("flight_Disable_Timer"));
	}

	public static void loadStrings() {
		lang.clear();
		lang.put("pluginPrefix", getString("pluginPrefix"));
		lang.put("flightOnMsg", getString("flightOnMsg"));
		lang.put("flightOffMsg", getString("flightOffMsg"));
		lang.put("noTownMsg", getString("noTownMsg"));
		lang.put("notInTownMsg", getString("notInTownMsg"));
		lang.put("flightDeactivatedMsg", getString("flightDeactivatedMsg"));
		lang.put("flightDeactivatedPVPMsg", getString("flightDeactivatedPVPMsg"));
		lang.put("flightDeactivatedConsoleMsg", getString("flightDeactivatedConsoleMsg"));
		lang.put("noPermission", getString("noPermission"));
		lang.put("missingNode", getString("missingNode"));
		lang.put("notDuringWar", getString("notDuringWar"));
		lang.put("returnToAllowedArea", getString("returnToAllowedArea"));
	}

	public static String getLangString(String languageString) {
		return lang.get(languageString);
	}

	public static boolean hasLangString(String languageString) {
		return lang.containsKey(languageString);
	}

	private static String getOption(String string) {
		return config.getConfig().getConfigurationSection("options").getString(string);
	}

	private static String getString(String string) {
		return ChatColor.translateAlternateColorCodes('&', config.getConfig().getConfigurationSection("language").getString(string));
	}
}
