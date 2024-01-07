package com.gmail.llmdlio.townyflight;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.enums.SiegeStatus;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.persistence.PersistentDataType;

public class TownyFlightAPI {

	private static TownyFlight plugin;
	private static TownyFlightAPI instance;
	public Set<Player> fallProtectedPlayers = ConcurrentHashMap.newKeySet();
	private static Map<UUID, Boolean> canFlyCache = new ConcurrentHashMap<>();
	private static NamespacedKey forceAllowFlight;
	private static Map<UUID, Integer> enemiesInTown = new ConcurrentHashMap<>();
	
	public TownyFlightAPI(TownyFlight plugin) {
		TownyFlightAPI.plugin = plugin;
		forceAllowFlight = new NamespacedKey(plugin, "force_allow_flight");

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

		// Get the town the player is currently standing in
		Town town = TownyAPI.getInstance().getTown(player.getLocation());

		//
		if (player.hasPermission("townyflight.bypass") 
			|| player.getGameMode().equals(GameMode.SPECTATOR) 
			|| player.getGameMode().equals(GameMode.CREATIVE)
			|| town != null && MetaData.getFreeFlightMeta(town)
			|| getForceAllowFlight(player))
			return true;
		if (!Permission.has(player, "townyflight.command.tfly", true)) return false;

		Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
		if (resident == null) return false;

		if (!resident.hasTown() && !locationTrustsResidents(town, resident)) {
			if (!silent) Message.of("noTownMsg").to(player);
			return false;
		}

		if (warPrevents(player.getLocation(), resident)) {
			if (!silent) Message.of("notDuringWar").to(player);
			return false;
		}

		if (!allowedLocation(player, player.getLocation(), resident)) {
			if (!silent) Message.of("notInTownMsg").to(player);
			return false;
		}

		// If the enemiesInTown hashmap contains this town and its value is >0, players cannot fly there.
		if(containsTown(town)){
			return false;
		}

		return true;
	}

	private boolean locationTrustsResidents(Town town, Resident resident) {
		return town != null && town.hasTrustedResident(resident);
	}


	/**
	 * Returns true if a player is allowed to fly at their current location. Checks
	 * if they are in the wilderness, in their own town and if not, whether they
	 * have the alliedtowns permission and if they are in an allied area.
	 * 
	 * @param player   The {@link Player}.
	 * @param location The {@link Location} to test for the player.
	 * @param resident The {@link Resident} of the {@link Player}.
	 * @return true if player is allowed to be flying at their present location.
	 */
	public static boolean allowedLocation(Player player, Location location, Resident resident) {
		if (instance.getForceAllowFlight(player))
			return true;

		if (TownyAPI.getInstance().isWilderness(location))
			return false;

		if (player.hasPermission("townyflight.alltowns"))
			return true;

		Town town = TownyAPI.getInstance().getTown(location);
		if (player.hasPermission("townyflight.trustedtowns") && town.hasTrustedResident(resident))
			return true;

		Town residentTown = resident.getTownOrNull();
		if (residentTown == null)
			return false;

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
				if (cause.equals("pvp")) reason = Message.getLangString("flightDeactivatedPVPMsg");
				if (cause.equals("console")) reason = Message.getLangString("flightDeactivatedConsoleMsg");
				if (cause.equals("enemynearby")) reason = Message.getLangString("flightDeactivatedEnemyNearbyMsg");
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
		cachePlayerFlight(player, false);
	}

	/**
	 * Turn flight on for a {@link Player}.
	 * 
	 * @param player {@link Player} who receives flight.
	 * @param silent true will mean no message is shown to the {@link Player}.
	 */
	public void addFlight(Player player, boolean silent) {
		// Added a canFly check
		if(canFly(player, true)){
			if (!silent) Message.of("flightOnMsg").to(player);
			player.setAllowFlight(true);
			cachePlayerFlight(player, true);
		}
	}

	/**
	 * Protects a player from receiving fall damage when their flight is revoked.
	 * 
	 * @param player {@link Player} who is losing their flight.
	 */
	public void protectFromFall(Player player) {
		fallProtectedPlayers.add(player);
		plugin.getScheduler().runAsyncLater(() -> removeFallProtection(player), 100);
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
	public static void takeFlightFromPlayersInTown(Town town, String cause) {
		for (final Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {


			if ((player.hasPermission("townyflight.bypass") || !player.getAllowFlight()
				|| !player.getAllowFlight()
				|| TownyAPI.getInstance().isWilderness(player.getLocation())
				|| !TownyAPI.getInstance().getTown(player.getLocation()).equals(town)
				|| TownyFlightAPI.getInstance().canFly(player, true)))
				continue;

			TownyFlightAPI.getInstance().removeFlight(player, false, true, cause);
		}	
	}

	public static void addFlightToPlayersInTown(Town town){
		for (final Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
			// If the player's town does not match the flight re-adding, dont add it.
			if(TownyAPI.getInstance().getTown(player) != town) {
				return;
			}

			// If they can already fly, dont add it
			if (player.getAllowFlight()) return;


			plugin.getScheduler().runLater(player, () -> {
				if (!TownyFlightAPI.getInstance().canFly(player, true)){
					return;
				}
				if (Settings.autoEnableFlight) {
					TownyFlightAPI.getInstance().addFlight(player, Settings.autoEnableSilent);
				}

				TownyFlightAPI.cachePlayerFlight(player, true);
			}, 1);

		}
	}

	/**
	 * This method allows the player to fly everywhere, even if they are not in a town. Useful for
	 * plugins that have items that can make you fly.
	 *
	 * @param player {@link Player} who is flying.
	 * @param force  true will allow the player to fly anywhere.
	 */
	public void setForceAllowFlight(Player player, boolean force) {
		player.getPersistentDataContainer().set(forceAllowFlight, PersistentDataType.BYTE, (byte) (force ? 1 : 0));
	}

	/**
	 * This method checks if the player is allowed to fly anywhere
	 *
	 * @param player {@link Player} who is being checked.
	 * @return true if the player is allowed to fly anywhere.
	 */
	public boolean getForceAllowFlight(Player player) {
		return player.getPersistentDataContainer().getOrDefault(forceAllowFlight, PersistentDataType.BYTE, (byte) 0) == 1;
	}

	private boolean warPrevents(Location location, Resident resident) {
		return Settings.disableDuringWar && (townHasActiveWar(location, resident) || residentIsSieged(location));
	}

	private static boolean townHasActiveWar(Location loc, Resident resident) {
		return (resident.hasTown() && resident.getTownOrNull().hasActiveWar()) || (!TownyAPI.getInstance().isWilderness(loc) && TownyAPI.getInstance().getTown(loc).hasActiveWar());
	}

	private static boolean residentIsSieged(Location location) {
		Town town = TownyAPI.getInstance().getTown(location);
		return Settings.siegeWarFound && town != null && SiegeController.hasSiege(town) && SiegeController.getSiege(town).getStatus().equals(SiegeStatus.IN_PROGRESS);
	}

	public static boolean canFlyAccordingToCache(Player player) {
		if (!canFlyCache.containsKey(player.getUniqueId()))
			cachePlayerFlight(player, TownyFlightAPI.getInstance().canFly(player, true));

		return canFlyCache.get(player.getUniqueId());
	}

	public static void cachePlayerFlight(Player player, boolean canFly) {
		if (canFlyCache.containsKey(player.getUniqueId()) && canFlyCache.get(player.getUniqueId()) == canFly)
			return;
		canFlyCache.put(player.getUniqueId(), canFly);
	}
	
	public static void removeCachedPlayer(Player player) {
		canFlyCache.remove(player.getUniqueId());
	}

	public static void incrementEnemiesInTown(Town town) {

		UUID uuid = town.getUUID();

		if(enemiesInTown.containsKey(uuid)){
			enemiesInTown.put(uuid, enemiesInTown.get(uuid) + 1);
        }
		else{
			enemiesInTown.put(uuid, 1);
        }
        takeFlightFromPlayersInTown(town, "enemynearby");

    }

	public static void decrementEnemiesInTown(Town town) {

		UUID uuid = town.getUUID();

		if(enemiesInTown.containsKey(uuid)){
			enemiesInTown.put(uuid, enemiesInTown.get(uuid) - 1);

			// Re-add flight if there are no more enemies in town.
			if(enemiesInTown.get(uuid) <= 0){
				addFlightToPlayersInTown(town);
			}
		}
		else{
			plugin.getServer().getLogger().severe("Tried to decrement enemies in town for a town that shouldn't have any enemies.");
		}
	}
	public static boolean containsTown(Town town) {

		UUID uuid = town.getUUID();

		if(enemiesInTown.containsKey(uuid) && enemiesInTown.get(uuid) > 0){
			return true;
		}
		else{
			return false;
		}
	}
}
