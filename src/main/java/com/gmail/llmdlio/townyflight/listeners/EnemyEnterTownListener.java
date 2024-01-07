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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EnemyEnterTownListener implements Listener {
	private final TownyFlight plugin;

	private final TownyFlightAPI api;

	public EnemyEnterTownListener(TownyFlight plugin, TownyFlightAPI api) {
		this.plugin = plugin;
		this.api = api;
	}

	/*
	 * Listener for when enemies enter a town.
	 * It will increment the enemiesInTown counter for the town.
	 */
	@EventHandler
	private void enemyEnterTownEvent(PlayerEntersIntoTownBorderEvent event) {
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());

		if (resident == null)
			return;


		final Town town = event.getEnteredTown();

		// Removes flight when anyone other than a town member enters your claim.
		if(playerDisablesFlight(town, resident)){
			api.incrementEnemiesInTown(town);
		}


	}

	/*
	 * Listener for when enemies leave a town.
	 * It will decrement the enemiesInTown counter for the town.
	 */
	@EventHandler
	private void enemyLeaveTownEvent(PlayerExitsFromTownBorderEvent event) {
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());

		if (resident == null)
			return;


		final Town town = event.getLeftTown();

		if(playerDisablesFlight(town, resident)){
			api.decrementEnemiesInTown(town);
		}
	}

	/*
	 * Listener for when enemies login within town grounds.
	 * It will increment the enemiesInTown counter for the town.
	 */
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

		if(playerDisablesFlight(town, resident)){
			api.incrementEnemiesInTown(town);
		}
	}

	/*
	 * Listener for when enemies log out inside a town.
	 * It will decrement the enemiesInTown counter for the town.
	 */
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

		if(playerDisablesFlight(town, resident)){
			api.decrementEnemiesInTown(town);
		}
	}

	/*
	 * Listener for when enemies teleport from a town.
	 * It will decrement the enemiesInTown counter for the town.
	 */
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

		if(playerDisablesFlight(town, resident)){
			api.decrementEnemiesInTown(town);
		}
	}

	/*
	 * A helper method which returns true if a player should disable flight depending on config setting.
	 */
	public boolean playerDisablesFlight(Town town, Resident resident){
		if (Settings.flightDisableBy.equals("ALLY")){

			// If they don't belong to a town, treated as a NEUTRAL player
			if(TownyAPI.getInstance().getResidentTownOrNull(resident) == null){
				return true;
			}

			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				return true;
			}

		}
		if(Settings.flightDisableBy.equals("NEUTRAL")){

			// If they don't belong to a town, treated as a NEUTRAL player
			if(TownyAPI.getInstance().getResidentTownOrNull(resident) == null){
				return true;
			}

			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) && !CombatUtil.isAlly(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				return true;
			}
		}

		if(Settings.flightDisableBy.equals("ENEMY")) {

			// If they don't belong to a town, treated as a NEUTRAL player
			if(TownyAPI.getInstance().getResidentTownOrNull(resident) == null){
				return false;
			}
			if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) || CombatUtil.isEnemy(TownyAPI.getInstance().getResidentTownOrNull(resident), town)) {
				return true;
			}
		}

		return false;
	}
}
