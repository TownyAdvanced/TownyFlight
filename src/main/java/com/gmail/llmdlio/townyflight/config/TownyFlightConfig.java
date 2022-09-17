package com.olziedev.terraeflight.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.olziedev.terraeflight.TownyFlight;
import com.palmergames.bukkit.config.CommentedConfiguration;

public class TownyFlightConfig {
	private TownyFlight plugin;
	private static CommentedConfiguration config;
	private static CommentedConfiguration newConfig;

	public TownyFlightConfig(TownyFlight plugin) {
		this.plugin = plugin;
	}

	public boolean reload() {
		return loadConfig();
	}

	// Method to load TownyFlight\config.yml
	private boolean loadConfig() {
		File f = new File(plugin.getDataFolder(), "config.yml");

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		config = new CommentedConfiguration(f.toPath());

		if (!config.load())
			return false;

		setDefaults(plugin.getDescription().getVersion(), f.toPath());

		config.save();

		return true;
	}

	/**
	 * Builds a new config reading old config data.
	 */
	private static void setDefaults(String version, Path configPath) {
		newConfig = new CommentedConfiguration(configPath);
		newConfig.load();

		for (ConfigNodes node : ConfigNodes.values()) {
			String root = node.getRoot();
			if (node.getComments().length > 0)
				addComment(root, node.getComments());

			if (root.equals(ConfigNodes.VERSION.getRoot()))
				setNewProperty(root, version);
			else
				setNewProperty(root, (config.get(root) != null) ? config.get(root) : node.getDefault());
		}

		config = newConfig;
		newConfig = null;
	}

	public CommentedConfiguration getConfig() {
		return config;
	}

	private static void addComment(String root, String... comments) {
		newConfig.addComment(root, comments);
	}

	private static void setNewProperty(String root, Object value) {
		if (value == null)
			value = "";
		newConfig.set(root, value.toString());
	}
}
