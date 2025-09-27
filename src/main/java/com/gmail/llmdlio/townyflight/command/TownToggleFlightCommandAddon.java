package com.gmail.llmdlio.townyflight.command;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.gmail.llmdlio.townyflight.TownyFlightAPI;
import com.gmail.llmdlio.townyflight.util.Message;
import com.gmail.llmdlio.townyflight.util.MetaData;
import com.gmail.llmdlio.townyflight.util.Permission;

import com.palmergames.bukkit.towny.TownyCommandAddonAPI;
import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownyCommandAddonAPI.CommandType;
import com.palmergames.bukkit.towny.command.BaseCommand;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.AddonCommand;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.NameUtil;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

public class TownToggleFlightCommandAddon extends BaseCommand implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player))
			return Collections.emptyList();

		switch (args[0].toLowerCase()) {
			case "flight":
				if (args.length == 2)
					return NameUtil.filterByStart(BaseCommand.setOnOffCompletes, args[1]);
				else
					return Collections.emptyList();
		}
		return Collections.emptyList();
	}

	public TownToggleFlightCommandAddon() {
		TownyCommandAddonAPI.addSubCommand(new AddonCommand(CommandType.TOWN_TOGGLE, "flight", this));
	}

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
		if (!(sender instanceof Player)) {
			TownyFlight.getPlugin().getLogger().warning("This command is not for Console use.");
			return true;
		}
		try {
			parseTownToggleFlightCommand(sender, args);
		} catch (TownyException e) {
			TownyMessaging.sendErrorMsg(sender, e.getMessage());
		}
		return true;
	}

	private void parseTownToggleFlightCommand(CommandSender sender, String[] args) throws TownyException {
		Resident resident = getResidentOrThrow((Player) sender);
		if (!resident.isMayor()) {
			Message.of("notMayorMsg").serious().to(sender);
			return;
		}

		if (!Permission.has(sender, "townyflight.command.town.toggle.flight", false))
			return;

		Town town = getTownFromResidentOrThrow(resident);
		boolean futurestate = !MetaData.getFreeFlightMeta(town);

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on")) {
				futurestate = true;
			} else if (args[0].equalsIgnoreCase("off")) {
				futurestate = false;
			}
		}

		MetaData.setFreeFlightMeta(town, futurestate);
		Message.of(String.format(Message.getLangString("townWideFlight"),
				Message.getLangString(futurestate ? "enabled" : "disabled"), town)).to(sender);
		if (!futurestate)
			TownyFlightAPI.getInstance().takeFlightFromPlayersInTown(town);
	}

}
