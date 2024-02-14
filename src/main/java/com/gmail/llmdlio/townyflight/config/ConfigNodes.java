package com.gmail.llmdlio.townyflight.config;

public enum ConfigNodes {
	VERSION("Version", ""),
	PLUGIN_PREFIX("pluginPrefix",
			"&8[&3TownyFlight&8] ",
			""),
	LANG("language", "", "",
			"####################",
			"# Language Strings #",
			"####################"),
	LANG_FLIGHTON(
			"language.flightOnMsg",
			"Flight Activated. ",
			"",
			"# Message shown when flight activated."),
	LANG_FLIGHTOFF(
			"language.flightOffMsg",
			"Flight De-Activated. ",
			"",
			"# Message shown when flight de-activated."),
	LANG_NOTOWN(
			"language.noTownMsg",
			"Flight cannot be activated, you don't belong to a town. ",
			"",
			"# Message shown when player lacks a town. "),
	LANG_NOTINTOWN(
			"language.notInTownMsg",
			"Flight cannot be activated, return to your town and try again. ",
			"",
			"# Message shown when flight cannot be turned on."),
	LANG_FLIGHTDEACTIVATEDMSG(
			"language.flightDeactivatedMsg",
			"Left town boundaries. ",
			"",
			"# Message shown when a player has flight taken away."),
	LANG_FLIGHTDEACTIVATEDPVPMSG(
			"language.flightDeactivatedPVPMsg",
			"Entering PVP combat. ",
			"",
			"# Message shown when a player has flight taken away because of PVP."),
	LANG_FLIGHTDEACTIVATEDCONSOLEMSG(
			"language.flightDeactivatedConsoleMsg",
			"Flight priviledges removed. ",
			"",
			"# Message shown when a player has flight taken away by console."),
	LANG_FLIGHTDEACTIVATEDTIMEMSG(
			"language.flightDeactivatedTimeMsg",
			"You ran out of flight time. ",
			"",
			"# Message shown when a player has flight taken away because their tempflight ran out."),
	LANG_NOPERMISSION(
			"language.noPermission",
			"You do not have permission for this command%s. ",
			"",
			"# Message shown when a player lacks a permission node."),
	LANG_MISSINGNODE(
			"language.missingNode",
			", missing %s",
			"",
			"# Message attached to noPermission when options.show_Permission_After_No_Permission_Message is true"),
	LANG_NOTDURINGWAR(
			"language.notDuringWar", 
			"You cannot use flight while Towny war is active. ",
			"",
			"# Message shown when war is active and flight is disallowed."),
	LANG_RETURNTOALLOWEDAREA(
			"language.returnToAllowedArea",
			"You have %s seconds to return to an allowed flight area. ",
			"",
			"# Message telling a player to return to an allowed flight area."),
	LANG_NOTOWNFOUND(
			"language.noTownFound",
			"TownyFlight cannot find a town by the name %s. ",
			"",
			"# Message when a town cannot be found by the name."),
	LANG_TOWNWIDEFLIGHT(
			"language.townWideFlight",
			"Free flight has been %s in %s. ",
			"",
			"# Message when a town has free flight enabled or disabled."),
	LANG_DISABLED(
			"language.disabled", 
			"disabled",
			"",
			"# The word disabled."),
	LANG_ENABLED(
			"language.enabled",
			"enabled",
			"",
			"# The world enabled."),
	LANG_STATUSSCREENCOMP(
			"language.statusScreenComponent", 
			"Free Flight",
			"",
			"# The component shown on towns' status screens when they have free flight enabled."),
	LANG_STATUSCOMPHOVER(
			"language.statusScreenComponentHover",
			"Flight enabled for everyone within this town's borders.",
			"",
			"# The hover text shown on the free flight status screen component."),
	
	OPTIONS("options", "", "",
			"#################", 
			"#    Options    #", 
			"#################"),
	OPTIONS_AUTO_ENABLE_FLIGHT(
			"options.auto_Enable_Flight",
			"false",
			"",
			"# If set to true, players entering their town will have flight auto-enabled.",
			"# When set to true, the plugin will use slightly more resources due to the EnterTown listener."),
	OPTIONS_AUTO_ENABLE_SILENT(
			"options.auto_Enable_Silent", 
			"false",
			"",
			"# If set to true, players entering their town will have flight auto-enabled without being notified in chat."),
	OPTIONS_DISABLE_DURING_WARTIME(
			"options.disable_During_Wartime", 
			"true",
			"",
			"# If set to false, players can still fly in their town while war is active."),
	OPTIONS_DISABLE_COMBAT_PREVENT(
			"options.disable_Combat_Prevention", 
			"false",
			"",
			"# If set to false, TownyFlight will not prevent combat of flying people."),
	OPTIONS_SHOW_PERMISSION(
			"options.show_Permission_After_No_Permission_Message", 
			"true",
			"",
			"# If set to false, the language.noPermission message will not display the permission node."),
	OPTIONS_DISABLE_TIME(
			"options.flight_Disable_Timer",
			"3",
			"",
			"# Number of seconds after leaving an allowed flight area before flight is taken away.", "# Set to 0 to take flight away immediately.");

	private final String Root;
	private final String Default;
	private String[] comments;

	ConfigNodes(String root, String def, String... comments) {

		this.Root = root;
		this.Default = def;
		this.comments = comments;
	}

	/**
	 * Retrieves the root for a config option
	 *
	 * @return The root for a config option
	 */
	public String getRoot() {

		return Root;
	}

	/**
	 * Retrieves the default value for a config path
	 *
	 * @return The default value for a config path
	 */
	public String getDefault() {

		return Default;
	}

	/**
	 * Retrieves the comment for a config path
	 *
	 * @return The comments for a config path
	 */
	public String[] getComments() {

		if (comments != null) {
			return comments;
		}

		String[] comments = new String[1];
		comments[0] = "";
		return comments;
	}
}
