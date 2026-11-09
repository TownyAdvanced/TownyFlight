package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;

public class PlayerTeleportListener implements Listener {
	private final TownyFlight plugin;

	public PlayerTeleportListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void playerTeleports(PlayerTeleportEvent event) {
		handlePlayerTeleportation(event.getCause(), event.getPlayer(), event.getTo());
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void entityTeleports(EntityTeleportEvent event) {
		if (event.getEntity() instanceof Player)
			return;
		handleVehicleTeleportation(TeleportCause.PLUGIN, event.getEntity());
	}

	public void handlePlayerTeleportation(TeleportCause cause, Player player, Location to) {
		if(!player.getAllowFlight()
				|| player.hasPermission("townyflight.bypass")
				|| flightAllowedDestination(player, to)) {
			return;
		}

		TownyFlightAPI.getInstance().removeFlight(player, false, true, String.valueOf(cause));
	}

	public void handleVehicleTeleportation(TeleportCause cause, Entity vehicle) {
		for (Entity passenger : vehicle.getPassengers()) {
			if (passenger instanceof Player player) {
				plugin.getScheduler().runLater(player, () -> handlePlayerTeleportation(cause, player, player.getLocation()), 1);
			}
			handleVehicleTeleportation(cause, passenger);
		}
	}

	private boolean flightAllowedDestination(Player player, Location loc) {
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		return resident != null && TownyFlightAPI.allowedLocation(player, loc, resident);
	}

}
