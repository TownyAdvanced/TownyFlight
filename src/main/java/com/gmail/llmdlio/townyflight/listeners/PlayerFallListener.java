package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;

public class PlayerFallListener implements Listener {
	
	public PlayerFallListener() {
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    private void playerFallEvent (EntityDamageEvent event) {

		if (event.getCause().equals(DamageCause.FALL) && event.getEntityType().equals(EntityType.PLAYER)) {			
		
			Player player = (Player) event.getEntity();
			if (TownyFlightAPI.getInstance().fallProtectedPlayers.contains(player)) {
				TownyFlightAPI.getInstance().fallProtectedPlayers.remove(player);
				event.setCancelled(true);
			}
		}
	}

}
