package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.llmdlio.townyflight.TownyFlight;

public class PlayerTeleportListener implements Listener {
	
	public PlayerTeleportListener() {
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
    private void playerTeleports(PlayerTeleportEvent event) {
	    if (event.getCause() != TeleportCause.PLUGIN || event.getCause() != TeleportCause.COMMAND)
	        return;
	    
	    Player player = event.getPlayer();
        if (player.hasPermission("townyflight.bypass")
            || !player.getAllowFlight()
            || TownyFlight.canFly(player, true)) {
            return;
        }
        
        TownyFlight.removeFlight(player, false, true, "");
	}

}
