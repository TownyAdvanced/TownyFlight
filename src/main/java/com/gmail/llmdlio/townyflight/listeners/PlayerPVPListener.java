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
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.utils.CombatUtil;


public class PlayerPVPListener implements Listener {
	
	@SuppressWarnings("unused")
	private final TownyFlight plugin;
	private Towny towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
	
	public PlayerPVPListener(TownyFlight instance) {
		plugin = instance;
	}
	
    /*
     * Listener for a player who joins the server successfully.
     * Check if flight is allowed where they are currently and if not, remove it.
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

    	Player player = (Player) attacker;
    	
    	if (player.getGameMode().equals(GameMode.CREATIVE)) {
    		event.setCancelled(true);
    		return;    		
    	}
    	
    	if (!player.getAllowFlight())
    		return;

    	if (!TownyUniverse.getDataSource().getWorld(player.getLocation().getWorld().getName()).isUsingTowny())
    		return;

    	if (CombatUtil.preventDamageCall(towny, attacker, defender))
    		return;

    	if (!event.isCancelled()) {
    		TownyFlight.toggleFlight(player, false, true, "pvp");
    		event.setCancelled(true);
    	}    	
    }
}
