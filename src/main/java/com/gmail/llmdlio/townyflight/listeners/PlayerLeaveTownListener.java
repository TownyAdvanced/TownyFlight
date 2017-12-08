package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;

public class PlayerLeaveTownListener implements Listener{	
	
	@SuppressWarnings("unused")
	private final TownyFlight plugin;
	
	public PlayerLeaveTownListener(TownyFlight instance) {
		plugin = instance;
	}
	
    /*
     * Listener for a player who leaves town.
     * If they are flying, flying is deactivated.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerLeftTownEvent (PlayerLeaveTownEvent event) {    	
    	Player player = event.getPlayer();
    	if (player.getAllowFlight())    		
    		TownyFlight.toggleFlight(player, false, true);
    }
}
