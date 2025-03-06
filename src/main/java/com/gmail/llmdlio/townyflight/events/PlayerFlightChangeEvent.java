package com.gmail.llmdlio.townyflight.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerFlightChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final boolean flightAllowed;

    public PlayerFlightChangeEvent(Player player, boolean flightAllowed) {
        this.player = player;
        this.flightAllowed = flightAllowed;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFlightAllowed() {
        return flightAllowed;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
