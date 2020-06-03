package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.utils.CombatUtil;


public class PlayerPVPListener implements Listener {
	

	private Towny towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
	
	public PlayerPVPListener() {
	}
	
    /*
     * Listener to turn off flight if flying player enters PVP combat. 
     * Runs only if the config.yml's disable_Combat_Prevention is set to true.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void playerPVPEvent (EntityDamageByEntityEvent event) throws NotRegisteredException {
    	Entity attacker = event.getDamager();
    	Entity defender = event.getEntity();

    	if (attacker instanceof Projectile) {
			ProjectileSource shooter = ((Projectile) attacker).getShooter();
			if (shooter instanceof Entity)
				attacker = (Entity) shooter;
    	}
    	
    	if ( !(attacker instanceof Player) || !(defender instanceof Player)) 
    		return;

    	Player attackingPlayer = (Player) attacker;
    	
    	if (attackingPlayer.getGameMode().equals(GameMode.CREATIVE)) {
    		event.setCancelled(true);
    		return;    		
    	}
    	
    	if (!attackingPlayer.getAllowFlight())
    		return;

    	if (!TownyAPI.getInstance().getDataSource().getWorld(attackingPlayer.getLocation().getWorld().getName()).isUsingTowny())
    		return;

    	if (CombatUtil.preventDamageCall(towny, attacker, defender))
    		return;

    	if (!event.isCancelled()) {
    		TownyFlight.removeFlight(attackingPlayer, false, true, "pvp");
    		event.setCancelled(true);
    	}    	
    }
}
