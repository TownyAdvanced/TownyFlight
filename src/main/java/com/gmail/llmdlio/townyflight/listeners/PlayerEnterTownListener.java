package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.event.PlayerEnterTownEvent;

public class PlayerEnterTownListener implements Listener {

	private final TownyFlight plugin;
	
	public PlayerEnterTownListener(TownyFlight instance) {
		plugin = instance;
	}
	
    /*
     * Listener for a player who enters town.
     * Used only if the config has auto-flight enabled.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerEnterTownEvent (PlayerEnterTownEvent event) {
    	final Player player = event.getPlayer();    	
    	// Do nothing to players who are already flying.
    	if (player.getAllowFlight())  
    		return;    	
    	plugin.getServer().getScheduler().runTask(plugin, new Runnable() {			
			public void run() {
				if (!TownyFlight.canFly(player, true))
		    		return;
		    	TownyFlight.addFlight(player, TownyFlight.autoEnableSilent);				
			};
    	});
	
    }
}
