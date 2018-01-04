package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.event.TownUnclaimEvent;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;


public class TownUnclaimListener implements Listener {

	@SuppressWarnings("unused")
	private final TownyFlight plugin;
	
	public TownUnclaimListener(TownyFlight instance) {
		plugin = instance;
	}
	
	/*
     * Listener for when players unclaim territory.
     * Will cause any player in that area to lose flight.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void TownUnclaimEvent (TownUnclaimEvent event) {    	
    	Town town = event.getTown();
    	WorldCoord wc = event.getWorldCoord();

    	// Cycle through players of the affected town, because multiple players could be in a plot that is unclaimed.
    	for (final Player player : TownyUniverse.getOnlinePlayers(town)) {
    		if (player.hasPermission("townyflight.bypass"))
	    		return;
    		if (!player.getAllowFlight())
    			return;
    		
    		WorldCoord pwc = new WorldCoord(player.getWorld().getName(), Coord.parseCoord(player.getLocation()));
    		if (!pwc.equals(wc))
    			return;
    		
    		TownyFlight.toggleFlight(player, false, true);
    	}	
    }
}
