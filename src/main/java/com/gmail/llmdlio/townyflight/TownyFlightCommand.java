package com.gmail.llmdlio.townyflight;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.Permissions;

public class TownyFlightCommand implements CommandExecutor {

	private TownyFlight plugin;

	public TownyFlightCommand(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		parseCommand(sender, args);
		return true;
	}
	
	private void parseCommand(CommandSender sender, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					// Reload the plugin.
					reloadPlugin(sender);
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					toggleFlightOnOther(sender, args[0]);
				}
			}
			return;
		}

		if (sender instanceof Player) {
			// We have more than just /tfly
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					// We have /tfly reload, test for permission node.
					if (Permissions.has(sender,"townyflight.command.tfly.reload", false))
						reloadPlugin(sender);
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					if (Permissions.has(sender, "townyflight.command.tfly.other", false))
						toggleFlightOnOther(sender, args[0]);
				}
				return;
			}

			// We have only /tfly
			if (!TownyFlightAPI.getInstance().canFly((Player) sender, false))
				return;

			toggleFlight((Player) sender, false, false, "");
		}
	}

	/**
	 * If flight is on, turn it off and vice versa
	 * 
	 * @param player {@link Player} toggling flight.
	 * @param silent true will mean no message is shown to the {@link Player}.
	 * @param forced true if this is a forced deactivation or not.
	 * @param cause  String cause of disabling flight ("", "pvp", "console").
	 */
	public void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
		if (player.getAllowFlight())
			TownyFlightAPI.getInstance().removeFlight(player, silent, forced, cause);
		else
			TownyFlightAPI.getInstance().addFlight(player, silent);
	}

	public void toggleFlightOnOther(CommandSender sender, String name) {
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

	public void reloadPlugin(CommandSender sender) {
		plugin.loadSettings();
		plugin.unregisterEvents();
		plugin.registerEvents();
		Message.of("TownyFlight Config & Listeners reloaded.").to(sender);
	}
}
