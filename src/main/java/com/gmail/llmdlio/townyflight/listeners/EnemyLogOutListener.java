package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EnemyLogOutListener implements Listener {
	private final TownyFlight plugin;

	public EnemyLogOutListener(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void enemyLogOutListener(PlayerQuitEvent event) {

		// Get the resident
		final Resident resident = TownyAPI.getInstance().getResident(event.getPlayer().getUniqueId());
		if (resident == null)
			return;

		// Get the town
		final Town town = TownyAPI.getInstance().getTown(event.getPlayer().getLocation());
		if (town == null)
			return;


		if(Settings.flightDisableBy.equals("ALLY")){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}

		if(Settings.flightDisableBy.equals("NEUTRAL")){
			if (!CombatUtil.isSameTown(town, TownyAPI.getInstance().getResidentTownOrNull(resident)) && !CombatUtil.isAlly(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}

		if(Settings.flightDisableBy.equals("ENEMY")){
			if (CombatUtil.isEnemy(town, TownyAPI.getInstance().getResidentTownOrNull(resident))) {
				plugin.decrementEnemiesInTown(town);
			}
		}
		plugin.getServer().getLogger().info("An enemy logged out");
	}
}
