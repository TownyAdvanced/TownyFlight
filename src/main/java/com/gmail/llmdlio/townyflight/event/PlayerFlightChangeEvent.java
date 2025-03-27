package com.gmail.llmdlio.townyflight.event;

import com.palmergames.bukkit.towny.event.CancellableTownyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerFlightChangeEvent extends CancellableTownyEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private final boolean flightAllowed;
    private boolean silent;

    public PlayerFlightChangeEvent(Player player, boolean flightAllowed, boolean silent) {
        this.player = player;
        this.flightAllowed = flightAllowed;
        this.silent = silent;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFlightAllowed() {
        return flightAllowed;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
