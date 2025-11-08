package com.gmail.llmdlio.townyflight.util;

import org.bukkit.command.CommandSender;

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
		TownyMessaging.sendMessage(sender, message.getMessage());
	}
}