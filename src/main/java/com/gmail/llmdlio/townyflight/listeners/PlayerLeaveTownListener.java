package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.util.Message;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;

public class PlayerLeaveTownListener implements Listener{	
	
	private final TownyFlight plugin;
	
	public PlayerLeaveTownListener(TownyFlight instance) {
		plugin = instance;
	}

	/*
	 * Listener for a player who leaves town. Runs one tick after the
	 * PlayerLeaveTownEvent in order to get the proper location.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerLeftTownEvent(PlayerLeaveTownEvent event) {
		Player player = event.getPlayer();
		if (!player.getAllowFlight() || player.hasPermission("townyflight.bypass"))
			return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> executeLeaveTown(player), 1);
	}

	/*
	 * If player has left the town borders into an area they cannot fly in, remove
	 * their flight. Handles the flightDisableTimer if in use.
	 */
	private void executeLeaveTown(Player player) {
		if (!TownyFlightAPI.getInstance().canFly(player, true)) {
			if (Settings.flightDisableTimer < 1) {
				TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
			} else {
				Message.of(String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer)).serious().to(player);
				Bukkit.getScheduler().runTaskLater(plugin, () -> TownyFlightAPI.getInstance().testForFlight(player, true), Settings.flightDisableTimer * 20);
			}
		}
	}
}
