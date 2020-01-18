package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.gmail.llmdlio.townyflight.TownyFlight;

public class PlayerFallListener implements Listener {
	
	public PlayerFallListener() {
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    private void playerFallEvent (EntityDamageEvent event) {

		if (!event.getCause().equals(DamageCause.FALL) && !(event.getEntityType().equals(EntityType.PLAYER))) 
			return;
		
		Player player = (Player) event.getEntity();
		if (TownyFlight.flyingPlayers.contains(player)) {
			TownyFlight.flyingPlayers.remove(player);
			event.setCancelled(true);
		}
	}

}
