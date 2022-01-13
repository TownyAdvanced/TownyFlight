package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

public class PlayerTeleportListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	private void playerTeleports(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.PLUGIN || event.getCause() != TeleportCause.COMMAND)
			return;

		Player player = event.getPlayer();
		if (player.hasPermission("townyflight.bypass") 
				|| !player.getAllowFlight()
				|| flightAllowedDestination(player, event.getTo())) {
			return;
		}

		TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
	}

	private boolean flightAllowedDestination(Player player, Location loc) {
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null || !resident.hasTown())
			return false;

		return TownyFlightAPI.allowedLocation(player, loc, resident.getTownOrNull());
	}

}
