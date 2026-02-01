package com.gmail.llmdlio.townyflight.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.llmdlio.townyflight.config.Settings;
import com.gmail.llmdlio.townyflight.config.Settings.MessageLocation;
import com.palmergames.bukkit.towny.TownyMessaging;

public class MessageBuilder {
	String message;
	boolean serious;

	public Message build() {
		return new Message(this);
	}

	public MessageBuilder serious() {
		this.serious = true;
		return this;
	}

	public void to(CommandSender sender) {
		Message message = build();
		MessageLocation location = Settings.messageLocation;
		to(sender, location);
	}

	public void to(CommandSender sender, MessageLocation location) {
		Message message = build();
		if (!(sender instanceof Player player)) {
			TownyMessaging.sendMessage(sender, message.getMessage());
			return;
		}
		switch (location) {
		case chat -> TownyMessaging.sendMessage(sender, message.getMessage());
		case actionbar -> TownyMessaging.sendActionBarMessageToPlayer(player, message.getMessage());
		case title -> TownyMessaging.sendTitle(player, "", message.getMessage());
		default -> TownyMessaging.sendMessage(sender, message.getMessage());
		}
	}
}