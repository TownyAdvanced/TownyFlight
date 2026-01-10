package com.gmail.llmdlio.townyflight.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.scheduling.ScheduledTask;

public class BossBarUtil {

	private static final Map<UUID, BossBarData> activeBossBars = new ConcurrentHashMap<>();

	/**
	 * Holds boss bar and its update task together.
	 */
	private static class BossBarData {
		final BossBar bossBar;
		final ScheduledTask task;
		final long startTime;
		final int durationMs;
		final String messageFormat;
		int lastDisplayedSeconds = -1;

		BossBarData(BossBar bossBar, ScheduledTask task, int durationSeconds, String messageFormat) {
			this.bossBar = bossBar;
			this.task = task;
			this.startTime = System.currentTimeMillis();
			this.durationMs = durationSeconds * 1000;
			this.messageFormat = messageFormat;
		}

		double getProgress() {
			long elapsed = System.currentTimeMillis() - startTime;
			return Math.max(0, 1.0 - ((double) elapsed / durationMs));
		}

		int getRemainingSeconds() {
			long elapsed = System.currentTimeMillis() - startTime;
			return Math.max(0, (int) Math.ceil((durationMs - elapsed) / 1000.0));
		}

		boolean isExpired() {
			return System.currentTimeMillis() - startTime >= durationMs;
		}
	}

	/**
	 * Shows a countdown boss bar to the player that decreases over time.
	 * 
	 * @param player The player to show the boss bar to.
	 * @param message The message to display on the boss bar (use %s for seconds placeholder).
	 * @param seconds The number of seconds to show the boss bar.
	 */
	public static void showCountdownBar(Player player, String message, int seconds) {
		// Remove any existing boss bar for this player
		removeBar(player);

		// Format initial message with seconds
		String initialTitle = message.contains("%s") ? String.format(message, seconds) : message + " " + seconds + "s";
		BossBar bossBar = Bukkit.createBossBar(initialTitle, BarColor.RED, BarStyle.SOLID);
		bossBar.setProgress(1.0);
		bossBar.addPlayer(player);

		// Single repeating task that updates every 2 ticks (100ms) instead of one task per tick
		ScheduledTask task = TownyFlight.getPlugin().getScheduler().runRepeating(player, () -> {
			BossBarData data = activeBossBars.get(player.getUniqueId());
			if (data == null || data.isExpired()) {
				removeBar(player);
				return;
			}
			data.bossBar.setProgress(data.getProgress());
			
			// Update title only when seconds change to reduce string operations
			int remaining = data.getRemainingSeconds();
			if (remaining != data.lastDisplayedSeconds) {
				data.lastDisplayedSeconds = remaining;
				String title = data.messageFormat.contains("%s") 
					? String.format(data.messageFormat, remaining) 
					: data.messageFormat + " " + remaining + "s";
				data.bossBar.setTitle(title);
			}
		}, 2L, 2L);

		activeBossBars.put(player.getUniqueId(), new BossBarData(bossBar, task, seconds, message));
	}

	/**
	 * Removes the boss bar from the player if one exists.
	 * 
	 * @param player The player to remove the boss bar from.
	 */
	public static void removeBar(Player player) {
		BossBarData data = activeBossBars.remove(player.getUniqueId());
		if (data != null) {
			data.task.cancel();
			data.bossBar.removeAll();
		}
	}

	/**
	 * Checks if the player currently has an active boss bar.
	 * 
	 * @param player The player to check.
	 * @return true if the player has an active boss bar.
	 */
	public static boolean hasActiveBar(Player player) {
		return activeBossBars.containsKey(player.getUniqueId());
	}

	/**
	 * Cleans up all active boss bars. Should be called on plugin disable.
	 */
	public static void cleanup() {
		for (BossBarData data : activeBossBars.values()) {
			data.task.cancel();
			data.bossBar.removeAll();
		}
		activeBossBars.clear();
	}
}
