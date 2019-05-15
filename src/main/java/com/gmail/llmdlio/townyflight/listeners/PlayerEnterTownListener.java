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
    	if (player.hasPermission("townyflight.bypass")) // Added in 1.2.5 to stop players with the bypass node from losing flight when entering a town. 
    		return;    	
    	plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {			
			public void run() {
				if (!TownyFlight.canFly(player, true))
		    		return;
		    	TownyFlight.toggleFlight(player, TownyFlight.autoEnableSilent, false, "");				
			};
    	},20);
	
    }
}
