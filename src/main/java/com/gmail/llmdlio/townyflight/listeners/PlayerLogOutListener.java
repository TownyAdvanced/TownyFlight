package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.tasks.TempFlightTask;

public class PlayerLogOutListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		TownyFlightAPI.getInstance().testForFlight(event.getPlayer(), true);
		TownyFlightAPI.removeCachedPlayer(event.getPlayer());
		TempFlightTask.logOutPlayerWithRemainingTempFlight(event.getPlayer());
	}
}
