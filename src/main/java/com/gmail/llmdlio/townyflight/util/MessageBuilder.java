package com.gmail.llmdlio.townyflight.util;

import org.bukkit.command.CommandSender;

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
		sender.sendMessage(message.getMessage());
	}
}