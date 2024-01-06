package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class EnemyTeleportListener implements Listener {
	private final TownyFlight plugin;

	public EnemyTeleportListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void enemyTeleports(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.PLUGIN || event.getCause() != TeleportCause.COMMAND)
			return;

		Town town = TownyAPI.getInstance().getTown(event.getFrom());
		if (town == null)
			return;

		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
		if (resident == null)
			return;

		// Removes flight when anyone other than a town member enters your claim.
		if(Settings.flightDisableBy == "ALLY"){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}

		// Removes flight when anyone other than a town member or ally enters your claim.
		if(Settings.flightDisableBy == "NEUTRAL"){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) && !CombatUtil.isAlly(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}

		// Removes flight only if an enemy enters your claim.
		if(Settings.flightDisableBy == "ENEMY"){
			if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}

	}

}
