package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.gmail.llmdlio.townyflight.Message;
import com.gmail.llmdlio.townyflight.Settings;
import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
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

    	executeLeaveTown(player);
    }

    /*
     * If player has left the town borders into an area they cannot fly in, remove their flight.
     * Handles the flightDisableTimer if in use.
     */
	private void executeLeaveTown(Player player) {
    	if (!TownyFlightAPI.getInstance().canFly(player, true)) {
    		if (Settings.flightDisableTimer < 1) 
    			TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
    		else {
    			Message.to(player, ChatColor.RED + String.format(Settings.returnToAllowedArea, Settings.flightDisableTimer));
    			Bukkit.getScheduler().runTaskLater(plugin, ()-> TownyFlightAPI.getInstance().testForFlight(player, true), Settings.flightDisableTimer * 20);
    		}
    	}
    }
}
