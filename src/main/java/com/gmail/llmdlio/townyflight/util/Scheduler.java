package com.gmail.llmdlio.townyflight.util;

import org.bukkit.Bukkit;

import com.gmail.llmdlio.townyflight.TownyFlight;

public class Scheduler {

	private static TownyFlight plugin = TownyFlight.getPlugin();

	public static void run(Runnable runnable, long delay) {
		if (delay != 0) {
			Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
		} else {
			Bukkit.getScheduler().runTask(plugin, runnable);
		}
	}
}
