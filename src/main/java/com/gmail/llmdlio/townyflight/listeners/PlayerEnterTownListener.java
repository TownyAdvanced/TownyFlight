package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.event.player.PlayerEntersIntoTownBorderEvent;

public class PlayerEnterTownListener implements Listener {
	private final TownyFlight plugin;

	public PlayerEnterTownListener(TownyFlight plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * Listener for a player who enters town. Used only if the config has
	 * auto-flight enabled.
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	private void playerEnterTownEvent(PlayerEntersIntoTownBorderEvent event) {
		final Player player = event.getPlayer();
		// Do nothing to players who are already flying.
		if (player.getAllowFlight()) return;
		
		plugin.getScheduler().runLater(player, () -> {
			if (!TownyFlightAPI.getInstance().canFly(player, true))
				return;
			if (Settings.autoEnableFlight)
				TownyFlightAPI.getInstance().addFlight(player, Settings.autoEnableSilent);

			TownyFlightAPI.cachePlayerFlight(player, true);
		}, 1);
	}
}
