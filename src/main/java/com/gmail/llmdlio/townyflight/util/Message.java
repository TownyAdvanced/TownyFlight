package com.olziedev.terraeflight.util;

import com.olziedev.terraeflight.config.Settings;

import net.md_5.bungee.api.ChatColor;

public class Message {

	private String message;
	private boolean serious;

	public Message(MessageBuilder builder) {
		this.message = builder.message;
		this.serious = builder.serious;
	}

	public String getMessage() {
		return getLangString("pluginPrefix") + (serious ? ChatColor.RED : "") +  message;
	}
	
	public static MessageBuilder of(String message) {
		MessageBuilder builder = new MessageBuilder();
		if (hasLangString(message))
			message = getLangString(message);
		builder.message = message;
		return builder;
	}

	public static MessageBuilder noPerms(String node) {
		MessageBuilder builder = new MessageBuilder();
		builder.message = String.format(getLangString("noPermission"), (Settings.showPermissionInMessage ? String.format(getLangString("missingNode"), node) : ""));
		builder.serious();
		return builder;
	}

	public static String getLangString(String message) {
		String langString = Settings.getLangString(message);
		if (langString == null) langString = "TownyFlight asked for a missing language string (" + message + ") please report this.";
		return langString;
	}

	private static boolean hasLangString(String message) {
		return Settings.hasLangString(message);
	}

}
