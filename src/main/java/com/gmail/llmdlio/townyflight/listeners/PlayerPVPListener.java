package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.event.damage.TownyPlayerDamagePlayerEvent;


public class PlayerPVPListener implements Listener {

	/*
	 * Listener to turn off flight if flying player enters PVP combat. Runs only if
	 * the config.yml's disable_Combat_Prevention is set to true.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void playerPVPEvent(TownyPlayerDamagePlayerEvent event) {
		Player attackingPlayer = event.getAttackingPlayer();
		Player defendingPlayer = event.getVictimPlayer();

		if (!attackingPlayer.getAllowFlight())
			return;

		if (attackingPlayer.getGameMode().equals(GameMode.CREATIVE) || !defendingPlayer.canSee(attackingPlayer)) {
			event.setCancelled(true);
			return;
		}

		TownyFlightAPI.getInstance().removeFlight(attackingPlayer, false, true, "pvp");
		event.setCancelled(true);
	}
}
