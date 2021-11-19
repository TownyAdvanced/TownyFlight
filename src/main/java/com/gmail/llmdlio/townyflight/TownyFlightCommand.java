package com.gmail.llmdlio.townyflight;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.messaging.Message;

public class TownyFlightCommand implements CommandExecutor {

	private TownyFlight plugin;

	public TownyFlightCommand(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {

			if (args[0].equalsIgnoreCase("reload")) {
				// Reload the plugin.
				return reloadPlugin(sender);
			} else {
				// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
				return toggleFlightOnOther(sender, args[0]);
			}
		}

		if (sender instanceof Player) {
			// We have more than just /tfly
			if (args.length != 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					// We have /tfly reload, test for permission node.
					if (!sender.hasPermission("townyflight.command.tfly.reload")) {
						Message.noPerms("townyflight.command.tfly.reload").to(sender);
						return true;
					}
					// Reload the plugin.
					return reloadPlugin(sender);
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly
					// {name}
					if (!sender.hasPermission("townyflight.command.tfly.other")) {
						Message.noPerms("townyflight.command.tfly.other").to(sender);;
						return true;
					}
					// Send the name off to attempt to remove their flight.
					return toggleFlightOnOther(sender, args[0]);
				}
			}

			// We have only /tfly
			if (!TownyFlightAPI.getInstance().canFly((Player) sender, false))
				return true;
			toggleFlight((Player) sender, false, false, "");
			return true;
		}
		return true;
	}

	/**
	 * If flight is on, turn it off and vice versa
	 * 
	 * @param player
	 * @param silent - show messages to player
	 * @param forced - whether this is a forced deactivation or not
	 * @param cause  - cause of disabling flight ("", "pvp", "console")
	 */
	@SuppressWarnings("deprecation")
	public void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
		if (player.getAllowFlight()) {
			if (!silent) {
				if (forced) {
					String reason = Message.getLangString("flightDeactivatedMsg");
					if (cause == "pvp")
						reason = Message.getLangString("flightDeactivatedPVPMsg");
					if (cause == "console")
						reason = Message.getLangString("flightDeactivatedConsoleMsg");
					Message.of(reason + Message.of("flightOffMsg")).to(player);
				} else {
					Message.of("flightOffMsg").to(player);
				}
			}
			if (player.isFlying()) {
				// As of 1.15 the below line does not seem to be reliable.
				player.setFallDistance(-100000);
				// As of 1.15 the below is required.
				if (!player.isOnGround()) {
					TownyFlightAPI.getInstance().addFallProtection(player);
					Bukkit.getScheduler().runTaskLater(plugin, () -> TownyFlightAPI.getInstance().removeFallProtection(player), 100);
				}
			}
			player.setAllowFlight(false);
		} else {
			if (!silent)
				Message.of("flightOffMsg").to(player);
			player.setAllowFlight(true);
		}
	}

	public boolean toggleFlightOnOther(CommandSender sender, String name) {

		Player player = Bukkit.getPlayerExact(name);
		if (player != null && player.isOnline())
			if (!player.getAllowFlight()) {
				Message.of("Player " + name + " is already unable to fly. Could not remove flight.").to(sender);
			} else {
				toggleFlight(player.getPlayer(), false, true, "console");
				Message.of("Flight removed from " + name + ".").to(sender);
			}
		else
			Message.of("Player " + name + " not found, or is offline. Could not remove flight.").to(sender);

		return true;
	}

	public boolean reloadPlugin(CommandSender sender) {
		plugin.loadSettings();
		plugin.unregisterEvents();
		plugin.registerEvents();
		Message.of("TownyFlight Config & Listeners reloaded.").to(sender);;
		return true;
	}
}
