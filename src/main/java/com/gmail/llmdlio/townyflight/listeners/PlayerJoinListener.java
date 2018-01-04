package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class PlayerJoinListener implements Listener {


	@SuppressWarnings("unused")
	private final TownyFlight plugin;
	
	public PlayerJoinListener(TownyFlight instance) {
		plugin = instance;
	}
	
    /*
     * Listener for a player who joins the server successfully.
     * Check if flight is allowed where they are currently and if not, remove it.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void playerJoinEvent (PlayerJoinEvent event) throws NotRegisteredException {    	
    	Player player = event.getPlayer();

    	if (!TownyFlight.canFly(player, true))
    		return;

    	if (TownyFlight.autoEnableFlight) {
   			TownyFlight.toggleFlight(player, false, false, "");
    	} else {
    		player.setFallDistance(-100000);
    	}	
    }	
}
