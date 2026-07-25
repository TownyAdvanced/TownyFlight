package com.gmail.llmdlio.townyflight.integrations;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.IEssentials;

public final class EssentialsIntegration {

	private static IEssentials essentials;

	private EssentialsIntegration() {
	}

	public static void initialize(Plugin plugin) {
		if (plugin instanceof IEssentials)
			essentials = (IEssentials) plugin;
	}

	public static boolean isAfk(Player player) {
		return essentials != null && essentials.getUser(player).isAfk();
	}
}
