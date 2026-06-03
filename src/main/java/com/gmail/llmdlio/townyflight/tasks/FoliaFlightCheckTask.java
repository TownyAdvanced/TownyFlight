package com.gmail.llmdlio.townyflight.tasks;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FoliaFlightCheckTask {

    private final TownyFlight plugin;
    private final Map<UUID, Long> pendingRemoval = new ConcurrentHashMap<>();
    private ScheduledTask task;

    public FoliaFlightCheckTask(TownyFlight plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) return;

        task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> checkFlightForAllPlayers(), 20L, 40L);
        plugin.getLogger().info("[TownyFlight] Folia flight-check task started.");
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        pendingRemoval.clear();
    }

    private void checkFlightForAllPlayers() {
        String delayString = plugin.getConfig().getString("options.flight_Disable_Timer", "0");
        int delaySeconds;
        try {
            delaySeconds = Integer.parseInt(delayString);
        } catch (NumberFormatException e) {
            delaySeconds = 0;
        }

        long removalDelayMillis = delaySeconds * 1000L;

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!player.getAllowFlight()) {
                pendingRemoval.remove(player.getUniqueId());
                continue;
            }

            if (player.hasPermission("townyflight.bypass")) {
                pendingRemoval.remove(player.getUniqueId());
                continue;
            }

            boolean canFlyNow = TownyFlightAPI.getInstance().canFly(player, true);

            if (!canFlyNow) {
                if (!pendingRemoval.containsKey(player.getUniqueId())) {
                    pendingRemoval.put(player.getUniqueId(), System.currentTimeMillis());
                }

                long startedFailing = pendingRemoval.get(player.getUniqueId());

                if (System.currentTimeMillis() - startedFailing >= removalDelayMillis) {
                    TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
                    pendingRemoval.remove(player.getUniqueId());
                }
            } else {
                pendingRemoval.remove(player.getUniqueId());
            }
        }
    }
}