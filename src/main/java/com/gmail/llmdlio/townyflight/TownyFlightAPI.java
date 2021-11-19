package com.gmail.llmdlio.townyflight;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.messaging.Message;
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
		if (instance == null) {
			instance = new TownyFlightAPI(TownyFlight.getPlugin());
		}
		return instance;
	}

	/**
	 * Returns true if a player can fly according to TownyFlight's rules.
	 * 
	 * @param player
	 * @param silent - show messages to player.
	 **/
	public boolean canFly(Player player, boolean silent) {
		if (player.hasPermission("townyflight.bypass") || player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE))
			return true;
		if (!player.hasPermission("townyflight.command.tfly")) {
			if (!silent) Message.noPerms("townyflight.command.tfly").to(player);;
			return false;
		}

		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null)
			return false;

		if (Settings.disableDuringWar && (townHasActiveWar(player, resident) || warsForTowny(resident) || residentIsSieged(resident))) {
			if (!silent) Message.of("notDuringWar").to(player);
			return false;
		}

		if (!resident.hasTown()) {
			if (!silent) Message.of("noTownMsg").to(player);
			return false;
		}

		if (!allowedLocation(player)) {
			if (!silent) Message.of("notInTownMsg").to(player);
			return false;
		}
		return true;
	}

	private static boolean townHasActiveWar(Player player, Resident resident) {
		return (resident.hasTown() && resident.getTownOrNull().hasActiveWar()) 
				|| (!TownyAPI.getInstance().isWilderness(player.getLocation()) && TownyAPI.getInstance().getTown(player.getLocation()).hasActiveWar());
	}

	private static boolean residentIsSieged(Resident resident) {
		if (!Settings.siegeWarFound)
			return false;
		if (!resident.hasTown())
			return false;

		return (SiegeController.hasActiveSiege(resident.getTownOrNull()));
	}

	private static boolean warsForTowny(Resident resident) {
		if (!Settings.warsForTownyFound)
			return false;
		if (!resident.hasTown())
			return false;
		if (TownyAPI.getInstance().getResidentNationOrNull(resident) == null)
			return false;
		if (com.aurgiyalgo.WarsForTowny.WarManager
				.getWarForNation(TownyAPI.getInstance().getResidentNationOrNull(resident)) != null)
			return true;
		return false;
	}

	/**
	 * Returns true if a player is allowed to fly at their current location. Blocks
	 * wilderness flight, then check if they are in their own town and if not,
	 * whether they have the alliedtowns permission and if they are in an allied
	 * area.
	 * 
	 * @param player
	 * @return true if player is allowed to be flying at their present location.
	 */
	private static boolean allowedLocation(Player player) {
		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null || !resident.hasTown())
			return false;

		if (TownyAPI.getInstance().isWilderness(player.getLocation()))
			return false;
		
		if (player.hasPermission("townyflight.alltowns"))
			return true;

		Town town = TownyAPI.getInstance().getTown(player.getLocation());
		Town residentTown = TownyAPI.getInstance().getResidentTownOrNull(resident); 
		if (residentTown.getUUID() == town.getUUID())
			return true;
		if (player.hasPermission("townyflight.alliedtowns"))
			return CombatUtil.isAlly(town, residentTown);
		return false;
	}

	/**
	 * Turn off flight.
	 * 
	 * @param player
	 * @param silent - show messages to player
	 * @param forced - whether this is a forced deactivation or not
	 * @param cause  - cause of disabling flight ("", "pvp", "console")
	 */
	@SuppressWarnings("deprecation")
	public void removeFlight(Player player, boolean silent, boolean forced, String cause) {
		if (!silent) {
			if (forced) {
				String reason = Message.getLangString("flightDeactivatedMsg");
				if (cause == "pvp")
					reason = Message.getLangString("flightDeactivatedPVPMsg");
				if (cause == "console")
					reason = Message.getLangString("flightDeactivatedConsoleMsg");
				Message.of(reason + Message.getLangString("flightOffMsg")).to(player);
			} else {
				Message.of("flightOffMsg").to(player);
			}
		}
		if (player.isFlying()) {
			// As of 1.15 the below line does not seem to be reliable.
			player.setFallDistance(-100000);
			// As of 1.15 the below is required.
			if (!player.isOnGround()) {
				addFallProtection(player);
				Bukkit.getScheduler().runTaskLater(plugin, () -> removeFallProtection(player), 100);
			}
		}
		player.setAllowFlight(false);
	}

	/**
	 * Turn flight on.
	 * 
	 * @param player
	 * @param silent - show messages to player
	 */
	public void addFlight(Player player, boolean silent) {
		if (!silent) Message.of("flightOnMsg").to(player);;
		player.setAllowFlight(true);
	}

	public void addFallProtection(Player player) {
		fallProtectedPlayers.add(player);
	}
	
	public void removeFallProtection(Player player) {
		if (fallProtectedPlayers.contains(player))
			fallProtectedPlayers.remove(player);
	}

	public void testForFlight(Player player, boolean silent) {
		if (!canFly(player, silent))
			removeFlight(player, false, true, "");
	}
}
