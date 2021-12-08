package com.gmail.llmdlio.townyflight;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;
import com.gmail.llmdlio.townyflight.util.Scheduler;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;

public class TownyFlightAPI {

	private static TownyFlight plugin;
	private static TownyFlightAPI instance;
	public Set<Player> fallProtectedPlayers = new HashSet<>();
	
	public TownyFlightAPI(TownyFlight _plugin) {
		plugin = _plugin;
	}
	
	public static TownyFlightAPI getInstance() {
		if (instance == null)
			instance = new TownyFlightAPI(plugin);
		return instance;
	}

	/**
	 * Returns true if a player can fly according to TownyFlight's rules.
	 * 
	 * @param player {@link Player} to test for flight allowance.
	 * @param silent true will show messages to player.
	 * @return true if the {@link Player} is allowed to fly.
	 **/
	public boolean canFly(Player player, boolean silent) {
		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		if (player.hasPermission("townyflight.bypass") 
			|| player.getGameMode().equals(GameMode.SPECTATOR) 
			|| player.getGameMode().equals(GameMode.CREATIVE)
			|| town != null && MetaData.getFreeFlightMeta(town))
			return true;

		if (!Permission.has(player, "townyflight.command.tfly", silent)) return false;

		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null) return false;

		if (!resident.hasTown()) {
			if (!silent) Message.of("noTownMsg").to(player);
			return false;
		}

		if (warPrevents(player.getLocation(), resident)) {
			if (!silent) Message.of("notDuringWar").to(player);
			return false;
		}

		if (!allowedLocation(player, resident.getTownOrNull())) {
			if (!silent) Message.of("notInTownMsg").to(player);
			return false;
		}
		return true;
	}

	/**
	 * Returns true if a player is allowed to fly at their current location. Checks
	 * if they are in the wilderness, in their own town and if not, whether they have the alliedtowns
	 * permission and if they are in an allied area.
	 * 
	 * @param player       the {@link Player}.
	 * @param residentTown The {@link Town} of the {@link Player}.
	 * @return true if player is allowed to be flying at their present location.
	 */
	private static boolean allowedLocation(Player player, Town residentTown) {
		if (TownyAPI.getInstance().isWilderness(player.getLocation()))
			return false;

		if (player.hasPermission("townyflight.alltowns"))
			return true;

		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		if (residentTown.getUUID() == town.getUUID())
			return true;

		if (player.hasPermission("townyflight.nationtowns") && CombatUtil.isSameNation(residentTown, town))
			return true;

		if (player.hasPermission("townyflight.alliedtowns") && CombatUtil.isAlly(town, residentTown))
			return true;

		return false;
	}

	/**
	 * Turn off flight from a {@link Player}.
	 * 
	 * @param player the {@link Player} to take flight from.
	 * @param silent true will mean no message is shown to the {@link Player}.
	 * @param forced true if this is a forced deactivation or not.
	 * @param cause  String cause of disabling flight ("", "pvp", "console").
	 */
	@SuppressWarnings("deprecation")
	public void removeFlight(Player player, boolean silent, boolean forced, String cause) {
		if (!silent) {
			if (forced) {
				String reason = Message.getLangString("flightDeactivatedMsg");
				if (cause == "pvp") reason = Message.getLangString("flightDeactivatedPVPMsg");
				if (cause == "console") reason = Message.getLangString("flightDeactivatedConsoleMsg");
				Message.of(reason + Message.getLangString("flightOffMsg")).to(player);
			} else {
				Message.of("flightOffMsg").to(player);
			}
		}
		if (player.isFlying()) {
			// As of 1.15 the below line does not seem to be reliable.
			player.setFallDistance(-100000);
			// As of 1.15 the below is required.
			if (!player.isOnGround())
				protectFromFall(player);
		}
		player.setAllowFlight(false);
	}

	/**
	 * Turn flight on for a {@link Player}.
	 * 
	 * @param player {@link Player} who receives flight.
	 * @param silent true will mean no message is shown to the {@link Player}.
	 */
	public void addFlight(Player player, boolean silent) {
		if (!silent) Message.of("flightOnMsg").to(player);
		player.setAllowFlight(true);
	}

	/**
	 * Protects a player from receiving fall damage when their flight is revoked.
	 * 
	 * @param player {@link Player} who is losing their flight.
	 */
	public void protectFromFall(Player player) {
		fallProtectedPlayers.add(player);
		Scheduler.run(() -> removeFallProtection(player), 100);
	}
	
	public boolean removeFallProtection(Player player) {
		return fallProtectedPlayers.remove(player);
	}

	/**
	 * Check if the {@link Player} is able to fly, and remove it if unabled.
	 * 
	 * @param player {@link Player} who is being tested.
	 * @param silent true will mean no message is shown to the {@link Player}.
	 */
	public void testForFlight(Player player, boolean silent) {
		if (!canFly(player, silent)) removeFlight(player, false, true, "");
	}

	/**
	 * Parse over the players online in the server and if they're in the given {@link Town},
	 * and are not given a flight bypass of some kind, remove their flight. Called when
	 * a town has their free flight disabled.
	 */
	public void takeFlightFromPlayersInTown(Town town) {
		for (final Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
			if (player.hasPermission("townyflight.bypass")
				|| !player.getAllowFlight()
				|| TownyAPI.getInstance().isWilderness(player.getLocation())
				|| !TownyAPI.getInstance().getTown(player.getLocation()).equals(town)
				|| TownyFlightAPI.getInstance().canFly(player, true))
				continue;

			TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
		}	
	}

	private boolean warPrevents(Location location, Resident resident) {
		return Settings.disableDuringWar && (townHasActiveWar(location, resident) || residentIsSieged(resident));
	}

	private static boolean townHasActiveWar(Location loc, Resident resident) {
		return resident.getTownOrNull().hasActiveWar() || !TownyAPI.getInstance().isWilderness(loc) && TownyAPI.getInstance().getTown(loc).hasActiveWar();
	}

	private static boolean residentIsSieged(Resident resident) {
		return Settings.siegeWarFound && SiegeController.hasActiveSiege(resident.getTownOrNull());
	}
}
