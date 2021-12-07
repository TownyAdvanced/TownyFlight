package com.gmail.llmdlio.townyflight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.NameUtil;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.StringMgmt;

public class TownyFlightCommand implements TabExecutor {

	private TownyFlight plugin;
	private CommandSender sender;
	private static final List<String> tflyTabCompletes = Arrays.asList(
		"reload","town","help","?"
	);

	public TownyFlightCommand(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length > 0) {
			switch (args[0].toLowerCase()) {
			case "town":
				if (args.length == 2)
					return getTownyStartingWith(args[1], "t");
				if (args.length == 3)
					return Collections.singletonList("toggleflight");
				break;
			default:
				if (args.length == 1)
					return filterByStartOrGetTownyStartingWith(tflyTabCompletes, args[0], "r");
				else 
					Collections.emptyList();
			}
		}
		return Collections.emptyList();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		this.sender = sender;
		parseCommand(args);
		return true;
	}
	
	private void parseCommand(String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
					showTflyHelp();
				} else if (args[0].equalsIgnoreCase("reload")) {
					// Reload the plugin.
					reloadPlugin();
				} else if (args[0].equalsIgnoreCase("town")) {
					// parse /tfly town NAME OPTION command.
					parseTownCommand(StringMgmt.remFirstArg(args));
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					toggleFlightOnOther(args[0]);
				}
			}
			return;
		}

		if (sender instanceof Player) {
			// We have more than just /tfly
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
					showTflyHelp();
				} else if (args[0].equalsIgnoreCase("reload") && Permission.has(sender,"townyflight.command.tfly.reload", false)) {
					// We have /tfly reload and tested for permission node.
					reloadPlugin();
				} else if (args[0].equalsIgnoreCase("town") && Permission.has(sender,"townyflight.command.tfly.town", false)) {
					// parse /tfly town NAME OPTION command.
					parseTownCommand(StringMgmt.remFirstArg(args));
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					if (Permission.has(sender, "townyflight.command.tfly.other", false))
						toggleFlightOnOther(args[0]);
				} 
				return;
			}

			// We have only /tfly
			if (!TownyFlightAPI.getInstance().canFly((Player) sender, false))
				return;

			toggleFlight((Player) sender, false, false, "");
		}
	}

	private void parseTownCommand(String[] args) {
		if (args.length < 2) { 
			showTflyHelp();
			return;
		}

		Town town = TownyAPI.getInstance().getTown(args[0]);
		if (town == null) {
			Message.of(String.format(Message.getLangString("noTownFound"), args[0])).serious().to(sender);
			return;
		}

		if (args[1].equalsIgnoreCase("toggleflight")) {
			boolean active = MetaData.getFreeFlightMeta(town);
			MetaData.setFreeFlightMeta(town, !active);
			Message.of(String.format(Message.getLangString("townWideFlight"), Message.getLangString(!active ? "enabled" : "disabled"), town)).to(sender);
			return;
		}

		showTflyHelp();
	}
	

	private void showTflyHelp() {
		if (Permission.has(sender, "townyflight.command.tfly", true))
			Message.of(Colors.White + "/tfly - Toggle flight.").to(sender);
		if (Permission.has(sender, "townyflight.command.tfly.reload", true))
			Message.of(Colors.White + "/tfly reload - Reload the TownyFlight config.").to(sender);
		if (Permission.has(sender, "townyflight.command.tfly.other", true))
			Message.of(Colors.White + "/tfly [playername] - Toggle flight for a player.").to(sender);
		if (Permission.has(sender, "townyflight.command.tfly.town", true))
			Message.of(Colors.White + "/tfly town [townname] toggleflight - Toggle free flight in the given town.").to(sender);
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

	public void reloadPlugin() {
		plugin.loadSettings();
		plugin.unregisterEvents();
		plugin.registerEvents();
		Message.of("TownyFlight Config & Listeners reloaded.").to(sender);
	}
	

	/**
	 * Returns a List<String> containing strings of resident, town, and/or nation names that match with arg.
	 * Can check for multiple types, for example "rt" would check for residents and towns but not nations or worlds.
	 *
	 * @param arg the string to match with the chosen type
	 * @param type the type of Towny object to check for, can be r(esident), t(own), n(ation), w(orld), or any combination of those to check
	 * @return Matches for the arg with the chosen type
	 */
	static List<String> getTownyStartingWith(String arg, String type) {

		List<String> matches = new ArrayList<>();
		TownyUniverse townyUniverse = TownyUniverse.getInstance();

		if (type.contains("r")) {
			matches.addAll(townyUniverse.getResidentsTrie().getStringsFromKey(arg));
		}

		if (type.contains("t")) {
			matches.addAll(townyUniverse.getTownsTrie().getStringsFromKey(arg));
		}

		if (type.contains("n")) {
			matches.addAll(townyUniverse.getNationsTrie().getStringsFromKey(arg));
		}

		if (type.contains("w")) { // There aren't many worlds so check even if arg is empty
			matches.addAll(NameUtil.filterByStart(NameUtil.getNames(townyUniverse.getWorldMap().values()), arg));
		}

		return matches;
	}
	
	/**
	 * Checks if arg starts with filters, if not returns matches from {@link #getTownyStartingWith(String, String)}. 
	 * Add a "+" to the type to return both cases
	 *
	 * @param filters the strings to filter arg with
	 * @param arg the string to check with filters and possibly match with Towny objects if no filters are found
	 * @param type the type of check to use, see {@link #getTownyStartingWith(String, String)} for possible types. Add "+" to check for both filters and {@link #getTownyStartingWith(String, String)}
	 * @return Matches for the arg filtered by filters or checked with type
	 */
	static List<String> filterByStartOrGetTownyStartingWith(List<String> filters, String arg, String type) {
		List<String> filtered = NameUtil.filterByStart(filters, arg);
		if (type.isEmpty())
			return filtered;
		else if (type.contains("+")) {
			filtered.addAll(getTownyStartingWith(arg, type));
			return filtered;
		} else {
			if (filtered.size() > 0) {
				return filtered;
			} else {
				return getTownyStartingWith(arg, type);
			}
		}
	}
}
