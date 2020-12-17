package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.event.town.TownUnclaimEvent;


public class TownUnclaimListener implements Listener {

	public TownUnclaimListener() {

	}
	
	/*
     * Listener for when players unclaim territory.
     * Will cause any player in that area to lose flight.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void TownUnclaimEvent (TownUnclaimEvent event) {    	
    	World world = event.getWorldCoord().getBukkitWorld();

    	// Cycle through players of the affected town, because multiple players could be in a plot that is unclaimed.
    	for (final Player player : Bukkit.getOnlinePlayers()) {
    		if (player.hasPermission("townyflight.bypass")
    		    || !player.getAllowFlight()
    		    || !player.getWorld().equals(world)
    		    || TownyFlight.canFly(player, true))
	    		return;
    		
    		TownyFlight.removeFlight(player, false, true, "");
    	}	
    }
}
