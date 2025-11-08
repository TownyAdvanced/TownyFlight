package com.gmail.llmdlio.townyflight.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;

public class TempFlightTask implements Runnable {

	private static Map<UUID, Long> playerUUIDSecondsMap = new ConcurrentHashMap<>();
	private int cycles = 0;

	@Override
	public void run() {
		cycles++;

		removeFlightFromPlayersWithNoTimeLeft();

		Set<UUID> uuidsToDecrement = new HashSet<>();
		for (Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
			if (!TownyAPI.getInstance().isTownyWorld(player.getWorld()))
				continue;
			if (Permission.has(player, "townyflight.bypass", true))
				continue;
			if (!player.getAllowFlight() || !player.isFlying())
				continue;
			UUID uuid = player.getUniqueId();
			if (!playerUUIDSecondsMap.containsKey(uuid))
				continue;
			uuidsToDecrement.add(uuid);
		}

		uuidsToDecrement.forEach(uuid -> decrementSeconds(uuid));
		if (cycles % 10 == 0)
			cycles = 0;
	}

	private void decrementSeconds(UUID uuid) {
		long seconds = playerUUIDSecondsMap.get(uuid);
		playerUUIDSecondsMap.put(uuid, --seconds);
		// Save players every 10 seconds;
		if (cycles % 10 == 0) {
			Resident resident = TownyAPI.getInstance().getResident(uuid);
			if (resident == null)
				return;
			MetaData.setSeconds(resident, seconds, true);
		}
	}

	private void removeFlightFromPlayersWithNoTimeLeft() {
		Set<UUID> uuidsToRemove = playerUUIDSecondsMap.entrySet().stream()
				.filter(e -> e.getValue() <= 0)
				.map(e -> e.getKey())
				.collect(Collectors.toSet());
		uuidsToRemove.forEach(uuid -> {
			removeFlight(uuid);
			Player player = Bukkit.getPlayer(uuid);
			if (player != null && player.isOnline())
				Message.of(String.format(Message.getLangString("yourTempFlightHasExpired"))).to(player);
		});
	}

	private void removeFlight(UUID uuid) {
		playerUUIDSecondsMap.remove(uuid);
		Player player = Bukkit.getPlayer(uuid);
		if (player != null && player.isOnline())
			TownyFlightAPI.getInstance().removeFlight(player, false, true, "time");
		MetaData.removeFlightMeta(uuid);
	}

	public static long getSeconds(UUID uuid) {
		return playerUUIDSecondsMap.containsKey(uuid) ? playerUUIDSecondsMap.get(uuid) : 0L;
	}

	public static void addPlayerTempFlightSeconds(UUID uuid, long seconds) {
		long existingSeconds = playerUUIDSecondsMap.containsKey(uuid) ? playerUUIDSecondsMap.get(uuid) : 0L;
		playerUUIDSecondsMap.put(uuid, existingSeconds + seconds);
	}

	public static void removeAllPlayerTempFlightSeconds(UUID uuid) {
		playerUUIDSecondsMap.put(uuid, 0L);
	}

	public static void logOutPlayerWithRemainingTempFlight(Player player) {
		if (!playerUUIDSecondsMap.containsKey(player.getUniqueId()))
			return;
		long seconds = playerUUIDSecondsMap.get(player.getUniqueId());
		if (seconds <= 0L)
			return;
		Resident resident = TownyAPI.getInstance().getResident(player);
		if (resident == null)
			return;
		MetaData.setSeconds(resident, seconds, true);
		playerUUIDSecondsMap.remove(player.getUniqueId());
	}
}
