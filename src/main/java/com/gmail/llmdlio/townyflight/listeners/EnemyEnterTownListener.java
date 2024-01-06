package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.player.PlayerEntersIntoTownBorderEvent;
import com.palmergames.bukkit.towny.event.player.PlayerExitsFromTownBorderEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
		if(PlayerDisablesFlight(town, resident)){
				TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
				plugin.incrementEnemiesInTown(town);
		}

		plugin.getServer().getLogger().info("An enemy entered the town");

	}

	@EventHandler
	private void enemyLeaveTownEvent(PlayerExitsFromTownBorderEvent event) {
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());

		if (resident == null)
			return;


		final Town town = event.getLeftTown();

		if(PlayerDisablesFlight(town, resident)){
			plugin.decrementEnemiesInTown(town);
		}

		plugin.getServer().getLogger().info("An enemy left the town");
	}

	@EventHandler
	public void enemyLogInListener(PlayerJoinEvent event) {

		// Get the resident
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
		if (resident == null)
			return;

		// Get the town
		final Town town = TownyAPI.getInstance().getTown(event.getPlayer().getLocation());
		if (town == null)
			return;

		if(PlayerDisablesFlight(town, resident)){
			plugin.incrementEnemiesInTown(town);
		}
	}

	@EventHandler
	public void enemyLogOutListener(PlayerQuitEvent event) {

		// Get the resident
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
		if (resident == null)
			return;

		// Get the town
		final Town town = TownyAPI.getInstance().getTown(event.getPlayer().getLocation());
		if (town == null)
			return;

		if(PlayerDisablesFlight(town, resident)){
			plugin.decrementEnemiesInTown(town);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void enemyTeleports(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN || event.getCause() != PlayerTeleportEvent.TeleportCause.COMMAND)
			return;

		Town town = TownyAPI.getInstance().getTown(event.getFrom());
		if (town == null)
			return;

		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
		if (resident == null)
			return;

		if(PlayerDisablesFlight(town, resident)){
			plugin.decrementEnemiesInTown(town);
		}
	}

	public boolean PlayerDisablesFlight(Town town, Resident resident){
		if (Settings.flightDisableBy.equals("ALLY")){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				return true;
			}

		}
		if(Settings.flightDisableBy.equals("NEUTRAL")){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) && !CombatUtil.isAlly(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				return true;
			}
		}

		if(Settings.flightDisableBy.equals("ENEMY")) {
			if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				return true;
			}
		}

		return false;
	}
}
