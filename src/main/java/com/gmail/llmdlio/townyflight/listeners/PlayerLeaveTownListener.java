package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;

public class PlayerLeaveTownListener implements Listener{	
	
	private final TownyFlight plugin;
	
	public PlayerLeaveTownListener(TownyFlight instance) {
		plugin = instance;
	}
	
    /*
     * Listener for a player who leaves town.
     * Runs one tick after the PlayerLeaveTownEvent in order to get the proper location.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerLeftTownEvent (PlayerLeaveTownEvent event) {    	
    	Player player = event.getPlayer();
    	if (!player.getAllowFlight() || player.hasPermission("townyflight.bypass"))
    		return;

    	new BukkitRunnable() {
			@Override
			public void run() {
				executeLeaveTown(player);
			}
		}.runTask(plugin);
    }

    /*
     * If player has left the town borders into an area they cannot fly in, remove their flight.
     * Handles the flightDisableTimer if in use.
     */
	private void executeLeaveTown(Player player) {
    	if (!TownyFlight.canFly(player, true)) {
    		if (TownyFlight.flightDisableTimer < 1) 
    			TownyFlight.removeFlight(player, false, true, "");
    		else {
    			player.sendMessage(TownyFlight.pluginPrefix + ChatColor.RED + String.format(TownyFlight.returnToAllowedArea, TownyFlight.flightDisableTimer));
    			new BukkitRunnable() {
					@Override
					public void run() {
						if (!TownyFlight.canFly(player, true))
							TownyFlight.removeFlight(player, false, true, "");
					}
				}.runTaskLater(plugin, TownyFlight.flightDisableTimer * 20);
    		}
    	}
    }
}
