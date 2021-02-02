package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;

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
            || flightAllowedDestination(player, event.getTo())) {
            return;
        }
        
        TownyFlight.removeFlight(player, false, true, "");
	}
	
	private boolean flightAllowedDestination(Player player, Location loc) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        if (resident == null)
            return false;
        
		if (TownyAPI.getInstance().isWilderness(loc))
			return false;

		if (player.hasPermission("townyflight.alltowns"))
			return true;

		try {
			Town town = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
			if (resident.getTown() == town)
				return true;
			if (player.hasPermission("townyflight.alliedtowns"))
				return CombatUtil.isAlly(town, resident.getTown());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		return false;		
	}

}
