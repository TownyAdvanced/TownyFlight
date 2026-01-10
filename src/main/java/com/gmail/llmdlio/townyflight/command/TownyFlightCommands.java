package com.gmail.llmdlio.townyflight.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.tasks.TempFlightTask;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.util.Colors;
import com.palmergames.util.TimeMgmt;
import com.palmergames.util.TimeTools;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TownyFlightCommands {

	private final TownyFlight plugin;

	public TownyFlightCommands(TownyFlight plugin) {
		this.plugin = plugin;
	}

	@Command("tfly|townyfly|townyflight")
	@CommandDescription("Toggle flight on or off")
	@Permission("townyflight.command.tfly")
	public void toggleFlight(Player player) {
		if (!TownyFlightAPI.getInstance().canFly(player, false))
			return;

		if (player.getAllowFlight())
			TownyFlightAPI.getInstance().removeFlight(player, false, false, "");
		else
			TownyFlightAPI.getInstance().addFlight(player, false);
	}

	@Command("tfly|townyfly|townyflight help|?")
	@CommandDescription("Show help for TownyFlight commands")
	public void showHelp(CommandSender sender) {
		if (sender.hasPermission("townyflight.command.tfly"))
			Message.of(Colors.White + "/tfly - Toggle flight.").to(sender);
		if (sender.hasPermission("townyflight.command.tfly.reload"))
			Message.of(Colors.White + "/tfly reload - Reload the TownyFlight config.").to(sender);
		if (sender.hasPermission("townyflight.command.tfly.tempflight")) {
			Message.of(Colors.White + "/tfly tempflight <player> <time> - Grant a player temp flight.").to(sender);
			Message.of(Colors.White + "/tfly tempflight <player> remove - Remove a player's temp flight.").to(sender);
		}
		if (sender.hasPermission("townyflight.command.tfly.other"))
			Message.of(Colors.White + "/tfly <player> - Toggle flight for a player.").to(sender);
		if (sender.hasPermission("townyflight.command.tfly.town"))
			Message.of(Colors.White + "/tfly town <townname> toggleflight - Toggle free flight in the given town.").to(sender);
	}

	@Command("tfly|townyfly|townyflight reload")
	@CommandDescription("Reload the TownyFlight configuration")
	@Permission("townyflight.command.tfly.reload")
	public void reloadConfig(CommandSender sender) {
		plugin.loadSettings();
		plugin.unregisterEvents();
		plugin.registerEvents();
		Message.of("TownyFlight Config & Listeners reloaded.").to(sender);
	}

	@Command("tfly|townyfly|townyflight tempflight <player> <time>")
	@CommandDescription("Grant temporary flight to a player")
	@Permission("townyflight.command.tfly.tempflight")
	public void grantTempFlight(
			CommandSender sender,
			@Argument(value = "player", suggestions = "residents") String playerName,
			@Argument(value = "time", suggestions = "timesuggestions") String time
	) {
		Player player = Bukkit.getPlayerExact(playerName);
		UUID uuid = player != null ? player.getUniqueId() : null;
		if (uuid == null && TownyUniverse.getInstance().hasResident(playerName)) {
			Resident resident = TownyAPI.getInstance().getResident(playerName);
			if (resident != null && resident.hasUUID())
				uuid = resident.getUUID();
		}

		if (uuid == null) {
			Message.of("Player " + playerName + " not found. Could not grant temp flight.").to(sender);
			return;
		}

		if (time.equalsIgnoreCase("remove")) {
			TempFlightTask.removeAllPlayerTempFlightSeconds(uuid);
			Message.of(playerName + " has had their flight time set to 0.").to(sender);
			return;
		}

		long seconds = parseSeconds(time, sender);
		if (seconds == 0L) {
			Message.of("Could not grant 0 seconds of temp flight.").to(sender);
			return;
		}

		String formattedTimeValue = TimeMgmt.getFormattedTimeValue(seconds * 1000L);
		Message.of(String.format(Message.getLangString("tempFlightGrantedToPlayer"), playerName, formattedTimeValue)).to(sender);
		MetaData.addTempFlight(uuid, seconds);

		if (player != null && player.isOnline()) {
			TempFlightTask.addPlayerTempFlightSeconds(uuid, seconds);
			Message.of(String.format(Message.getLangString("youHaveReceivedTempFlight"), formattedTimeValue)).to(player);

			if (Settings.autoEnableFlight && TownyFlightAPI.getInstance().canFly(player, true))
				TownyFlightAPI.getInstance().addFlight(player, Settings.autoEnableSilent);
		}
	}

	@Command("tfly|townyfly|townyflight town <town> toggleflight")
	@CommandDescription("Toggle free flight in a town")
	@Permission("townyflight.command.tfly.town")
	public void toggleTownFlight(
			CommandSender sender,
			@Argument(value = "town", suggestions = "towns") String townName
	) {
		Town town = TownyAPI.getInstance().getTown(townName);
		if (town == null) {
			Message.of(String.format(Message.getLangString("noTownFound"), townName)).serious().to(sender);
			return;
		}

		boolean futurestate = !MetaData.getFreeFlightMeta(town);
		MetaData.setFreeFlightMeta(town, futurestate);
		Message.of(String.format(Message.getLangString("townWideFlight"), Message.getLangString(futurestate ? "enabled" : "disabled"), town)).to(sender);
		if (!futurestate)
			TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
	}

	@Command("tfly|townyfly|townyflight <player>")
	@CommandDescription("Toggle flight for another player")
	@Permission("townyflight.command.tfly.other")
	public void toggleFlightOnOther(
			CommandSender sender,
			@Argument(value = "player", suggestions = "onlineplayers") String playerName
	) {
		Player player = Bukkit.getPlayerExact(playerName);
		if (player != null && player.isOnline()) {
			if (!player.getAllowFlight()) {
				Message.of("Player " + playerName + " is already unable to fly. Could not remove flight.").to(sender);
			} else {
				if (player.getAllowFlight())
					TownyFlightAPI.getInstance().removeFlight(player, false, true, "console");
				else
					TownyFlightAPI.getInstance().addFlight(player, false);
				Message.of("Flight removed from " + playerName + ".").to(sender);
			}
		} else {
			Message.of("Player " + playerName + " not found, or is offline. Could not remove flight.").to(sender);
		}
	}

	private long parseSeconds(String string, CommandSender sender) {
		if (string.endsWith("s") || string.endsWith("m") || string.endsWith("h") || string.endsWith("d"))
			return TimeTools.getSeconds(string);

		long seconds;
		try {
			seconds = Long.valueOf(string);
		} catch (NumberFormatException e) {
			Message.of("The number " + string + " cannot be parsed into a number of seconds.").to(sender);
			return 0L;
		}
		return seconds;
	}

	// Suggestion providers
	@Suggestions("residents")
	public List<String> suggestResidents(CommandContext<CommandSender> ctx, String input) {
		return TownyUniverse.getInstance().getResidentsTrie().getStringsFromKey(input);
	}

	@Suggestions("towns")
	public List<String> suggestTowns(CommandContext<CommandSender> ctx, String input) {
		return TownyUniverse.getInstance().getTownsTrie().getStringsFromKey(input);
	}

	@Suggestions("onlineplayers")
	public List<String> suggestOnlinePlayers(CommandContext<CommandSender> ctx, String input) {
		return Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
				.collect(Collectors.toList());
	}

	@Suggestions("timesuggestions")
	public List<String> suggestTime(CommandContext<CommandSender> ctx, String input) {
		return Stream.of("remove", "10", "1000s", "60m", "1h", "1d")
				.filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
				.collect(Collectors.toList());
	}
}
