package com.gmail.llmdlio.townyflight.tasks;

import com.gmail.llmdlio.townyflight.TownyFlight;
import com.palmergames.bukkit.towny.scheduling.ScheduledTask;

public class TaskHandler {

	private static ScheduledTask tempFlightTask = null;
	private static Runnable tempFlightRunnable = null;

	public static void toggleTempFlightTask(boolean on) {
		if (on && !isTempFlightTaskRunning()) {
			if (tempFlightRunnable == null)
				tempFlightRunnable = new TempFlightTask();
			tempFlightTask = TownyFlight.getPlugin().getScheduler().runRepeating(tempFlightRunnable, 60L, 20L);
		} else if (!on && isTempFlightTaskRunning()) {
			tempFlightTask.cancel();
			tempFlightTask = null;
			tempFlightRunnable = null;
		}
	}

	public static boolean isTempFlightTaskRunning() {
		return tempFlightTask != null && !tempFlightTask.isCancelled();
	}
}
