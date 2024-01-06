package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.player.PlayerEntersIntoTownBorderEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EnemyEnterTownListener implements Listener {
	private final TownyFlight plugin;

	public EnemyEnterTownListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	/*
	 * Listener which takes flight from a town's online players if an enemy enters
	 * into the town.
	 *
	 */
	@EventHandler
	private void enemyEnterTownEvent(PlayerEntersIntoTownBorderEvent event) {
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());

		if (resident == null)
			return;


		final Town town = event.getEnteredTown();

		// Removes flight when anyone other than a town member enters your claim.
		if(Settings.flightDisableBy == "ALLY"){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
				plugin.incrementEnemiesInTown(town);
			}
		}

		// Removes flight when anyone other than a town member or ally enters your claim.
		if(Settings.flightDisableBy == "NEUTRAL"){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) && !CombatUtil.isAlly(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
				plugin.incrementEnemiesInTown(town);
			}
		}

		// Removes flight only if an enemy enters your claim.
		if(Settings.flightDisableBy == "ENEMY"){
			if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
				plugin.incrementEnemiesInTown(town);
			}
		}

	}
}