package com.olziedev.terraeflight.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.olziedev.terraeflight.TownyFlightAPI;

public class PlayerFallListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled=true)
	private void playerFallEvent(EntityDamageEvent event) {

		if (event.getCause().equals(DamageCause.FALL) && event.getEntityType().equals(EntityType.PLAYER))
			event.setCancelled(TownyFlightAPI.getInstance().removeFallProtection((Player) event.getEntity()));
	}
}
