package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.palmergames.bukkit.towny.object.Resident;

public class TownRemoveResidentListener implements Listener {
	private final TownyFlight plugin;

	public TownRemoveResidentListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	/*
	 * Listener for a player who stops being a resident of a town.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerLeftTownEvent(TownRemoveResidentEvent event) {
		Resident resident = event.getResident();
		if (!resident.isOnline())
			return;
		Player player = resident.getPlayer();
		if (!player.getAllowFlight() || player.hasPermission("townyflight.bypass"))
			return;

		plugin.getScheduler().runLater(player, () -> testPlayer(player), 1);
	}

	/*
	 * Check if the player is allowed to fly at their location.
	 */
	private void testPlayer(Player player) {
		if (!TownyFlightAPI.getInstance().canFly(player, true)) {
			TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
		}
	}
}
