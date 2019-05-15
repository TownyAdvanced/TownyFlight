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
	
	// Method to load TownyFlight\config.yml
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
    	
    	addComment("language", "", "",
				"####################",
				"# Language Strings #",
				"####################","");
    	
    	addComment("language.flightOnMsg","# Message shown when flight activated.");
    	addDefault("language.flightOnMsg", "Flight Activated.");
    	addComment("language.flightOffMsg","# Message shown when flight de-activated.");
    	addDefault("language.flightOffMsg", "Flight De-activated.");
    	addComment("language.noTownMsg","# Message shown when player lacks a town.");
    	addDefault("language.noTownMsg", "Flight cannot be activated, you don't belong to a town.");
    	addComment("language.notInTownMsg","# Message shown when flight cannot be turned on.");
    	addDefault("language.notInTownMsg", "Flight cannot be activated, return to your town and try again.");
    	addComment("language.flightDeactivatedMsg","# Message shown when a player has flight taken away.");
    	addDefault("language.flightDeactivatedMsg", "Left town boundaries. ");
    	addComment("language.flightDeactivatedPVPMsg","# Message shown when a player has flight taken away because of PVP.");
    	addDefault("language.flightDeactivatedPVPMsg", "Entering PVP combat. ");
    	addComment("language.noPermission","# Message shown when a player lacks a permission node.");
    	addDefault("language.noPermission", "You do not have permission for this command, missing: ");
    	addComment("language.notDuringWar", "# Message shown when war is active and flight is disallowed.");
    	addDefault("language.notDuringWar", "You cannot use flight while Towny war is active.");
    	
    	addComment("options", "", "",
    						"#################",
    						"#    Options    #",
    						"#################","");
    	addComment("options.auto_Enable_Flight","# If set to true, players entering their town will have flight auto-enabled.",
                                                "# When set to true, the plugin will use slightly more resources due to the EnterTown listener.");
    	addDefault("options.auto_Enable_Flight","false");
    	addComment("options.auto_Enable_Silent","# If set to true, players entering their town will have flight auto-enabled without being notified in chat.");
    	addDefault("options.auto_Enable_Silent","false");
    	addComment("options.disable_During_Wartime","# If set to false, players can still fly in their town while war is active.");
    	addDefault("options.disable_During_Wartime","true");
    	addComment("options.disable_Combat_Prevention","# If set to false, TownyFlight will not prevent combat of flying people.");
    	addDefault("options.disable_Combat_Prevention","false");
	addComment("options.show_Permission_After_No_Permission_Message","# If set to false, the language.noPermission message will not display the permission node.");
	addDefault("options.show_Permission_After_No_Permission_Message","true");
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
