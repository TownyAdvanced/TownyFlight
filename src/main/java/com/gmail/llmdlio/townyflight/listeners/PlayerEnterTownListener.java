package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.PlayerEnterTownEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;

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
				if (!TownyFlightAPI.getInstance().canFly(player, true))
		    		return;
		    	TownyFlightAPI.getInstance().addFlight(player, Settings.autoEnableSilent);
			};
    	});
    }

    /*
     * Listener which takes flight from a town's online
     * players if an enemy enters into the town. 
     * 
     * TODO: Set this up in the config as an option.
     */
//    @EventHandler
    @SuppressWarnings("unused")
	private void enemyEnterTownEvent (PlayerEnterTownEvent event) {
    	final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
    	if (resident == null || !resident.hasTown())
    		return;
    	final Town town = event.getEnteredtown();
    	
    	if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
    		TownyAPI.getInstance().getOnlinePlayersInTown(town).stream()
    			.filter(player -> player.getAllowFlight())
    			.filter(player -> TownyAPI.getInstance().getTown(player.getLocation()).equals(town))
    			.forEach(player -> TownyFlightAPI.getInstance().removeFlight(player, false, true , ""));
    	}
    }
}
