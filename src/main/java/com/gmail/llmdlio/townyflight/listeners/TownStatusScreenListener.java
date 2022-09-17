package com.olziedev.terraeflight.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.olziedev.terraeflight.util.Message;
import com.olziedev.terraeflight.util.MetaData;
import com.palmergames.adventure.text.Component;
import com.palmergames.adventure.text.event.HoverEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.object.Translation;

public class TownStatusScreenListener implements Listener {
	private final Component comp = Component
			.text(Translation.of("status_format_key_value_key") + Message.getLangString("statusScreenComponent"))
			.hoverEvent(HoverEvent.showText(Component.text(Message.getLangString("statusScreenComponentHover"))));

	@EventHandler
	public void on(TownStatusScreenEvent event) {
		if (MetaData.getFreeFlightMeta(event.getTown()))
			event.getStatusScreen().addComponentOf("freeflight", comp);
	}
}
