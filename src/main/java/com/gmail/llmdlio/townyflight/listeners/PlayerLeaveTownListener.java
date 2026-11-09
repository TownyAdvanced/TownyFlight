package com.gmail.llmdlio.townyflight.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.config.Settings.MessageLocation;
import com.gmail.llmdlio.townyflight.util.Message;
import com.palmergames.bukkit.towny.event.player.PlayerExitsFromTownBorderEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;

public class PlayerLeaveTownListener implements Listener {
	private final TownyFlight plugin;

	public PlayerLeaveTownListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	/*
	 * Listener for a player who leaves town. Runs one tick after the
	 * PlayerLeaveTownEvent in order to get the proper location.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerLeftTownEvent(PlayerExitsFromTownBorderEvent event) {
		scheduleFlightCheck(event.getPlayer());
	}

	/*
	 * Paper (seemingly) does not fire PlayerMoveEvents for a player who is nested below another
	 * entity in a vehicle's passenger tree (e.g., a player riding a pig in
	 * a boat).
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void vehicleCrossesTownBlock(VehicleMoveEvent event) {
		if (!WorldCoord.cellChanged(event.getFrom(), event.getTo()))
			return;

		Set<UUID> checkedPlayers = new HashSet<>();
		for (Entity passenger : event.getVehicle().getPassengers())
			scheduleNestedPassengerChecks(passenger, 1, checkedPlayers);
	}

	private void scheduleNestedPassengerChecks(Entity passenger, int depth, Set<UUID> checkedPlayers) {
		if (depth > 1 && passenger instanceof Player player && checkedPlayers.add(player.getUniqueId()))
			scheduleFlightCheck(player);

		for (Entity nestedPassenger : passenger.getPassengers())
			scheduleNestedPassengerChecks(nestedPassenger, depth + 1, checkedPlayers);
	}

	private void scheduleFlightCheck(Player player) {
		if (!TownyFlightAPI.canFlyAccordingToCache(player) || player.hasPermission("townyflight.bypass"))
			return;

		plugin.getScheduler().runLater(player, () -> executeLeaveTown(player), 5);
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
				if (!Settings.returnToTownMessageAppearsInTitle)
					Message.of(String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer)).serious().to(player);
				else
					Message.of(String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer)).serious().to(player, MessageLocation.title);
				plugin.getScheduler().runLater(player, () -> TownyFlightAPI.getInstance().testForFlight(player, true), Settings.flightDisableTimer * 20);
			}
		}
	}
}
