package com.gmail.llmdlio.townyflight.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;

import com.gmail.llmdlio.townyflight.TownyFlight;


public class TownyFlightConfig {
	private TownyFlight plugin;
	private CommentedYamlConfiguration config;
	@SuppressWarnings("unused")
	private String newline = System.getProperty("line.separator");
	 
	public TownyFlightConfig(TownyFlight plugin){
		this.plugin = plugin;
	}
	
	public void reload(){
		loadConfig();
	}
	
	// Method to load UndeadRiders\config.yml
    private void loadConfig(){ 
        File f = new File(plugin.getDataFolder(), "config.yml"); 
         
        if(!f.exists()) { 
            try { 
                f.createNewFile(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
        } 
         
        config = new CommentedYamlConfiguration();        

        try { 
            config.load(f); 
        } catch (FileNotFoundException e) { 
            e.printStackTrace(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } catch (InvalidConfigurationException e) { 
            e.printStackTrace(); 
        } 
        
        addComment("Version","# TownyFlight by LlmDl.");          
        addDefault("Version", plugin.getDescription().getVersion()); 
        
        
    	addComment("pluginPrefix","# Prefix to messages seen in game.");
    	addDefault("pluginPrefix", "&8[&3TownyFlight&8] ");
    	
    	addComment("flightOnMsg","# Message shown when flight activated.");
    	addDefault("flightOnMsg", "Flight Activated.");
    	addComment("flightOffMsg","# Message shown when flight de-activated.");
    	addDefault("flightOffMsg", "Flight De-activated.");
    	addComment("noTownMsg","# Message shown when player lacks a town.");
    	addDefault("noTownMsg", "Flight cannot be activated, you don't belong to a town.");
    	addComment("notInTownMsg","# Message shown when flight cannot be turned on.");
    	addDefault("notInTownMsg", "Flight cannot be activated, return to your town and try again.");
    	addComment("flightDeactivatedMsg","# Message shown when a player has flight taken away.");
    	addDefault("flightDeactivatedMsg", "Left town boundaries, flight deactivated.");
    	
        // Write back config 
        try { 
            config.save(f); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
	
	public CommentedYamlConfiguration getConfig() {	
		return config;
	}
	
	private boolean hasPath(String path) {
		return config.isSet(path);
	}
	
	private void addComment(String path, String... comment) {
			config.addComment(path, comment);		
	}
	
	private void addDefault(String path, Object defaultValue) {
		if (!hasPath(path))
			config.set(path, defaultValue);		
	}
}