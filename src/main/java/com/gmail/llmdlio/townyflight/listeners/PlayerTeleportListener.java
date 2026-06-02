package com.gmail.llmdlio.townyflight.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.config.Settings.MessageLocation;
import com.gmail.llmdlio.townyflight.util.Message;

public class PlayerTeleportListener implements Listener {

    private final TownyFlight plugin;

    public PlayerTeleportListener(TownyFlight plugin) {
        this.plugin = plugin;
        plugin.getLogger().info("[TownyFlight DEBUG] PlayerTeleportListener CONSTRUCTOR called - listener is now loaded!");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void playerTeleports(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        plugin.getLogger().info("[TownyFlight DEBUG] TeleportEvent fired for " + player.getName()
                + " | Cause: " + event.getCause());

        // Folia-safe with extra delay and full protection
        plugin.getScheduler().runLater(player, () -> {
            try {
                executeTeleportCheck(player);
            } catch (Exception e) {
                plugin.getLogger().severe("[TownyFlight ERROR] Exception in teleport check for " + player.getName());
                e.printStackTrace();
            }
        }, 15); // slightly higher delay for Folia stability
    }

    private void executeTeleportCheck(Player player) {
        plugin.getLogger().info("[TownyFlight DEBUG] Running safe teleport check for " + player.getName()
                + " at " + player.getLocation());

        try {
            if (!TownyFlightAPI.canFlyAccordingToCache(player) || player.hasPermission("townyflight.bypass"))
                return;

            if (!TownyFlightAPI.getInstance().canFly(player, true)) {
                plugin.getLogger().info("[TownyFlight DEBUG] → Player left allowed area → disabling flight");

                if (Settings.flightDisableTimer < 1) {
                    TownyFlightAPI.getInstance().removeFlight(player, false, true, "");
                } else {
                    if (!Settings.returnToTownMessageAppearsInTitle)
                        Message.of(String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer)).serious().to(player);
                    else 
                        Message.of(String.format(Message.getLangString("returnToAllowedArea"), Settings.flightDisableTimer)).serious().to(player, MessageLocation.title);
                    plugin.getScheduler().runLater(player, () -> TownyFlightAPI.getInstance().testForFlight(player, true), Settings.flightDisableTimer * 20);
                }
            } else {
                plugin.getLogger().info("[TownyFlight DEBUG] → Still allowed to fly");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("[TownyFlight ERROR] Exception inside executeTeleportCheck for " + player.getName());
            e.printStackTrace();
        }
    }
}