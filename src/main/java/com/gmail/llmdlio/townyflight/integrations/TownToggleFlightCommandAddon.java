package com.gmail.llmdlio.townyflight.integrations;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class TownToggleFlightCommandAddon implements CommandExecutor {

    private final TownyFlight plugin;


    public TownToggleFlightCommandAddon(TownyFlight plugin) {
        this.plugin = plugin;
        TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN_TOGGLE, "flight", this));
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player) {
            Resident resident = TownyAPI.getInstance().getResident((Player)sender);
            if (resident == null || !resident.isMayor()) {
                Message.of(String.format(Message.getLangString("notMayorMsg"))).serious().to(sender);
                return true;
            }

            if(!Permission.has(sender,"townyflight.toggleflight", false)) return false;

            Town town = resident.getTownOrNull();

            boolean futurestate = !MetaData.getFreeFlightMeta(town);

            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("on")) {
                    futurestate = true;
                } else if (args[0].equalsIgnoreCase("off")) {
                    futurestate = false;
                }
            }

            MetaData.setFreeFlightMeta(town, futurestate);
            Message.of(String.format(Message.getLangString("townWideFlight"), Message.getLangString(futurestate ? "enabled" : "disabled"), town)).to(sender);
            if (!futurestate)
                TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
            return true;
        }
        return true;
    }
}
