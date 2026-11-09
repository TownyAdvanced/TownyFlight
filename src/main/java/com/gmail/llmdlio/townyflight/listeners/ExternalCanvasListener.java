package com.gmail.llmdlio.townyflight.listeners;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import io.canvasmc.canvas.event.EntityPostPortalAsyncEvent;
import io.canvasmc.canvas.event.EntityPostTeleportAsyncEvent;
import io.canvasmc.canvas.event.EntityTeleportAsyncEvent;
import io.canvasmc.canvas.event.PlayerRespawnAsyncEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ExternalCanvasListener implements Listener {

    private final PlayerTeleportListener delegate;
    private final TownyFlight plugin;

    public ExternalCanvasListener(PlayerTeleportListener delegate, TownyFlight plugin) {
        this.delegate = delegate;
        this.plugin = plugin;
    }

    @EventHandler
    public void playerTeleport(EntityTeleportAsyncEvent event) {
        if (event.getEntity() instanceof Player player) {
            delegate.handlePlayerTeleportation(event.getCause(), player, event.getTo());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void vehicleTeleport(EntityPostTeleportAsyncEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            delegate.handleVehicleTeleportation(event.getCause(), event.getEntity());
        }
    }

    // Flight will persist through portals if this isn't called.
    @EventHandler
    public void playerPortalTeleport(EntityPostPortalAsyncEvent event) {
        PlayerTeleportEvent.TeleportCause cause;
        switch (event.getPortalType()) {
            case NETHER -> cause = PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
            case ENDER -> cause = PlayerTeleportEvent.TeleportCause.END_PORTAL;
            case END_GATEWAY -> cause = PlayerTeleportEvent.TeleportCause.END_GATEWAY;
            default -> cause = PlayerTeleportEvent.TeleportCause.PLUGIN;
        }
        if (event.getEntity() instanceof Player player) {
            plugin.getScheduler().runLater(player, () -> delegate.handlePlayerTeleportation(cause, player, player.getLocation()), 1);
        } else {
            delegate.handleVehicleTeleportation(cause, event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerRespawnEvent(PlayerRespawnAsyncEvent event) {
        Player victim = event.getPlayer();
        if(!victim.getAllowFlight()
                || victim.hasPermission("townyflight.bypass")
                || flightAllowedDestination(victim, event.getRespawnLocation())) {
            return;
        }

        TownyFlightAPI.getInstance().removeFlight(victim, false, true, "death");

    }

    private boolean flightAllowedDestination(Player player, Location loc) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
        return resident != null && TownyFlightAPI.allowedLocation(player, loc, resident);
    }
}
