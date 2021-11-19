package com.gmail.llmdlio.townyflight.util;

import org.bukkit.command.CommandSender;

public class Permission {

	public static boolean has(CommandSender sender, String node, boolean silent) {
		if (sender.hasPermission(node))
			return true;
		if (!silent) Message.noPerms("townyflight.command.tfly.reload").to(sender);
		return false;
	}

}
