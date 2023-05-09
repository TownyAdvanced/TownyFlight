package com.gmail.llmdlio.townyflight.listeners;

import java.util.ArrayList;
import java.util.List;

import com.gmail.llmdlio.townyflight.TownyFlight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;


public class TownUnclaimListener implements Listener {
	private final TownyFlight plugin;

	public TownUnclaimListener(TownyFlight plugin) {
		this.plugin = plugin;
	}
	
	/*
	 * Listener for when players unclaim territory. Will cause any player in that
	 * area to lose flight.
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	private void TownUnclaimEvent(TownUnclaimEvent event) {
		plugin.getScheduler().runLater(() -> scanForFlightAbilities(selectArea(event.getWorldCoord())), 2);
	}

	private void scanForFlightAbilities(List<WorldCoord> plots) {
		
		// Cycle through all the online players, because multiple players could be in a plot that is unclaimed.
		for (final Player player : new ArrayList<>(Bukkit.getOnlinePlayers())) {
			plugin.getScheduler().run(player, () -> {
				if (player.hasPermission("townyflight.bypass")
						|| !player.getAllowFlight()
						|| !plots.contains(WorldCoord.parseWorldCoord(player))
						|| TownyFlightAPI.getInstance().canFly(player, true))
					return;

				TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
			});
    	}	
	}
	
	private List<WorldCoord> selectArea(WorldCoord centre) {
		List<WorldCoord> plots = new ArrayList<>(9);
		plots.add(centre.add(-1, -1));
		plots.add(centre.add(-1, 0));
		plots.add(centre.add(-1, 1));
		plots.add(centre.add(0, -1));
		plots.add(centre);
		plots.add(centre.add(0, 1));
		plots.add(centre.add(1, -1));
		plots.add(centre.add(1, 0));
		plots.add(centre.add(1, 1));
		
		return plots;
	}
}
