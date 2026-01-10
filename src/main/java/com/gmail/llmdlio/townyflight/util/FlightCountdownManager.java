package com.gmail.llmdlio.townyflight.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.scheduling.ScheduledTask;

/**
 * Manages flight disable countdown tasks for players leaving valid flight areas.
 */
public class FlightCountdownManager {

	private static final Map<UUID, ScheduledTask> activeCountdowns = new ConcurrentHashMap<>();

	/**
	 * Starts a countdown to disable flight for the player.
	 * If a countdown is already active, it will be cancelled and restarted.
	 * 
	 * @param player The player to start the countdown for.
	 */
	public static void startCountdown(Player player) {
		// Cancel any existing countdown first
		cancelCountdown(player);

		String warningMessage = String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer);

		// Show boss bar countdown
		BossBarUtil.showCountdownBar(player, warningMessage, Settings.flightDisableTimer);

		// Schedule the flight removal task
		ScheduledTask task = TownyFlight.getPlugin().getScheduler().runLater(player, () -> {
			activeCountdowns.remove(player.getUniqueId());
			BossBarUtil.removeBar(player);
			TownyFlightAPI.getInstance().testForFlight(player, true);
		}, Settings.flightDisableTimer * 20L);

		activeCountdowns.put(player.getUniqueId(), task);
	}

	/**
	 * Cancels any active countdown for the player.
	 * 
	 * @param player The player to cancel the countdown for.
	 */
	public static void cancelCountdown(Player player) {
		ScheduledTask task = activeCountdowns.remove(player.getUniqueId());
		if (task != null) {
			task.cancel();
		}
		BossBarUtil.removeBar(player);
	}

	/**
	 * Checks if the player has an active countdown.
	 * 
	 * @param player The player to check.
	 * @return true if the player has an active countdown.
	 */
	public static boolean hasActiveCountdown(Player player) {
		return activeCountdowns.containsKey(player.getUniqueId());
	}

	/**
	 * Cleans up all active countdowns. Should be called on plugin disable.
	 */
	public static void cleanup() {
		for (ScheduledTask task : activeCountdowns.values()) {
			task.cancel();
		}
		activeCountdowns.clear();
	}
}
