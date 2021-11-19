package com.gmail.llmdlio.townyflight.util;

import com.gmail.llmdlio.townyflight.config.Settings;

import net.md_5.bungee.api.ChatColor;

public class Message {

	static String pluginPrefix;
	public String message;
	private boolean serious;

	public Message(MessageBuilder builder) {
		this.message = builder.message;
		this.serious = builder.serious;
	}

	public String getMessage() {
		return getLangString("pluginPrefix") + (isSerious() ? ChatColor.RED : "") +  message;
	}
	
	public boolean isSerious() {
		return serious;
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
