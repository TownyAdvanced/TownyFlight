package com.gmail.llmdlio.townyflight;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Message {

	public static void to(CommandSender sender, String message) {
		sender.sendMessage(Settings.pluginPrefix + message);
	}
	
	public static void noPerms(CommandSender sender, String node) {
		to(sender, ChatColor.RED + Settings.noPermission + ((Settings.showPermissionInMessage) ? node : ""));
	}

}
