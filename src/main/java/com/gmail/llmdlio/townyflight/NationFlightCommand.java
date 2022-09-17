package com.olziedev.terraeflight;

import com.olziedev.terraeflight.util.Message;
import com.olziedev.terraeflight.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class NationFlightCommand implements CommandExecutor {

    private CommandSender sender;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        parseCommand(args);
        return true;
    }

    private void parseCommand(String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        if (sender instanceof Player) {
            // We have more than just /nfly
            if (args.length > 0) {
                // It's not any other subcommand of /nfly so handle removing flight via /nfly {name}
                if (Permission.has(sender, "townyflight.command.nfly.other", false))
                    toggleFlightOnOther(args[0]);
                return;
            }
            // We have only /nfly
            TownyFlightAPI.setFlightNation((Player) sender, true);
            if (!TownyFlightAPI.getInstance().canFly((Player) sender, false))
                return;

            toggleFlight((Player) sender, false, false, "");
        }
    }

    public void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
        if (player.getAllowFlight())
            TownyFlightAPI.getInstance().removeFlight(player, silent, forced, cause);
        else
            TownyFlightAPI.getInstance().addFlight(player, silent);
    }

    public void toggleFlightOnOther(String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null && player.isOnline()) {
            if (!player.getAllowFlight()) {
                Message.of("Player " + name + " is already unable to fly. Could not remove flight.").to(sender);
            } else {
                toggleFlight(player.getPlayer(), false, true, "console");
                Message.of("Flight removed from " + name + ".").to(sender);
            }
        } else {
            Message.of("Player " + name + " not found, or is offline. Could not remove flight.").to(sender);
        }
    }
}
