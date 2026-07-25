package com.gmail.llmdlio.townyflight.listeners;

import io.canvasmc.canvas.event.EntityPostPortalAsyncEvent;
import io.canvasmc.canvas.event.EntityTeleportAsyncEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ExternalCanvasListener implements Listener {

    private final PlayerTeleportListener delegate;

    public ExternalCanvasListener(PlayerTeleportListener delegate) {
        this.delegate = delegate;
    }

    @EventHandler
    public void playerTeleport(EntityTeleportAsyncEvent event) {
        if (event.getEntity() instanceof Player player) {
            delegate.handlePlayerTeleportation(event.getCause(), player, event.getTo());
        }
    }

    // Flight will persist through portals if this isn't called.
    @EventHandler
    public void playerPortalTeleport(EntityPostPortalAsyncEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerTeleportEvent.TeleportCause cause;
            switch (event.getPortalType()) {
                case NETHER -> cause = PlayerTeleportEvent.TeleportCause.NETHER_PORTAL;
                case ENDER -> cause = PlayerTeleportEvent.TeleportCause.END_PORTAL;
                case END_GATEWAY -> cause = PlayerTeleportEvent.TeleportCause.END_GATEWAY;
                default -> cause = PlayerTeleportEvent.TeleportCause.PLUGIN;
            }
            delegate.handlePlayerTeleportation(cause, player, player.getLocation());
        }
    }
}
