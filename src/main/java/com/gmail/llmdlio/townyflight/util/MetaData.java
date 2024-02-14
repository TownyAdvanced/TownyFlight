package com.gmail.llmdlio.townyflight.util;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;
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

	private static LongDataField tempFlightSeconds = new LongDataField("townyflight_tempflightseconds");


	public static void addTempFlight(UUID uuid, long seconds) {
		Resident resident = TownyAPI.getInstance().getResident(uuid);
		if (resident == null)
			return;
		addTempFlight(resident, seconds);
	}

	public static void addTempFlight(Resident resident, long seconds) {
		long existingSeconds = MetaDataUtil.getLong(resident, tempFlightSeconds);
		MetaDataUtil.setLong(resident, tempFlightSeconds, existingSeconds + seconds, true);
	}

	public static long getSeconds(UUID uuid) {
		Resident resident = TownyAPI.getInstance().getResident(uuid);
		if (resident == null)
			return 0L;
		return getSeconds(resident);
	}
	
	private static long getSeconds(Resident resident) {
		return MetaDataUtil.getLong(resident, tempFlightSeconds);
	}

	public static void setSeconds(Resident resident, long seconds, boolean save) {
		MetaDataUtil.setLong(resident, tempFlightSeconds, seconds, save);
	}

	public static void removeFlightMeta(UUID uuid) {
		Resident resident = TownyAPI.getInstance().getResident(uuid);
		if (resident == null)
			return;
		removeFlightMeta(resident);
	}

	public static void removeFlightMeta(Resident resident) {
		resident.removeMetaData(tempFlightSeconds);
	}
}
