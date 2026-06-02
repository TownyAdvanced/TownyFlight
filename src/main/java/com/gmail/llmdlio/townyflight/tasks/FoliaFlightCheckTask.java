package com.gmail.llmdlio.townyflight.tasks;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class FoliaFlightCheckTask {

    private final TownyFlight plugin;
    private ScheduledTask task;

    public FoliaFlightCheckTask(TownyFlight plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (task != null) {
            return;
        }

        task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> checkFlightForAllPlayers(), 20L, 10L);
        plugin.getLogger().info("[TownyFlight] Folia flight-check task started (handles HuskHomes/teleports)");
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void checkFlightForAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getAllowFlight() && !player.hasPermission("townyflight.bypass")) {
                if (!TownyFlightAPI.getInstance().canFly(player, true)) {
                    TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
                }
            }
        }
    }
}