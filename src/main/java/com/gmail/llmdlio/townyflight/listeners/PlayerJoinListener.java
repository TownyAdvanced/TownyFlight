package com.olziedev.terraeflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.olziedev.terraeflight.TownyFlightAPI;
import com.olziedev.terraeflight.config.Settings;
import com.olziedev.terraeflight.util.Scheduler;

public class PlayerJoinListener implements Listener {

	/*
	 * Listener for a player who joins the server successfully. Check if flight is
	 * allowed where they are currently and if not, remove it.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	private void playerJoinEvent(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		Scheduler.run(() -> {
			boolean canFly = TownyFlightAPI.getInstance().canFly(player, true);
			boolean isFlying = player.isFlying();
			if (isFlying && canFly)
				return;

			if (isFlying && !canFly) {
				TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
				return;
			}

			if (!isFlying && canFly && Settings.autoEnableFlight)
				TownyFlightAPI.getInstance().addFlight(player, false);
		}, 1);
	}
}
