package com.olziedev.terraeflight.util;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.utils.MetaDataUtil;

public class MetaData {

	private static BooleanDataField freeFlight = new BooleanDataField("townyflight_freeflight");

	public static boolean getFreeFlightMeta(Town town) {
		return com.palmergames.bukkit.towny.utils.MetaDataUtil.getBoolean(town, freeFlight);
	}
	
	public static void setFreeFlightMeta(Town town, boolean active) {
		if (!town.hasMeta("townyflight_freeflight")) {
			MetaDataUtil.addNewBooleanMeta(town, "townyflight_freeflight", active, true);
			return;
		} 
		MetaDataUtil.setBoolean(town, freeFlight, active, true);
	}
}
