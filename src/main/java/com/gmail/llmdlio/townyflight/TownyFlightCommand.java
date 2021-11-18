package com.gmail.llmdlio.townyflight;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class TownyFlightCommand implements TabExecutor {

	private TownyFlight plugin;
	public TownyFlightCommand(TownyFlight plugin) {
		this.plugin = plugin; 
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("tfly")) {

			if (sender instanceof ConsoleCommandSender) {

				if (args[0].equalsIgnoreCase("reload")) {
					// Reload the plugin.
					return reloadPlugin(sender);
				} else {
					// It's not any other subcommand of /tfly so handle removing flight via /tfly {name}
					return toggleFlightOnOther(sender, args[0]);
				}
			}

		 	if (sender instanceof Player) {

		 	    if (args.length != 0) {
		 	    	// We have more than just /tfly
		 	    	
		 	    	if (args[0].equalsIgnoreCase("reload")) {
			 	    	// We have /tfly reload, test for permission node.
		 	    		if (!sender.hasPermission("townyflight.command.tfly.reload")) {
		 	    			Message.noPerms(sender, "townyflight.command.tfly.reload");
							return true;
						}
		 	    		// Reload the plugin.
						return reloadPlugin(sender);
		 	    	} else {
		 	    		//  It's not any other subcommand of /tfly so handle removing flight via /tfly {name}		 	    		
		 	    		if (!sender.hasPermission("townyflight.command.tfly.other")) {
		 	    			Message.noPerms(sender, "townyflight.command.tfly.other");
							return true;
		 	    		}
		 	    		// Send the name off to attempt to remove their flight.
		 	    		return toggleFlightOnOther(sender, args[0]);
		 	    	}
		 	    }
		 	    
		 	    // We have only /tfly
                if (!TownyFlightAPI.getInstance().canFly((Player) sender, false))
                    return true;
                toggleFlight((Player) sender, false, false, "");
                return true;
			}
		}
		return false;
    }
	

	/**
     * If flight is on, turn it off and vice versa
     * 
     * @param player
     * @param silent - show messages to player
     * @param forced - whether this is a forced deactivation or not
     * @param cause - cause of disabling flight ("", "pvp", "console")
     */
    @SuppressWarnings("deprecation")
    public void toggleFlight(Player player, boolean silent, boolean forced, String cause) {
    	if (player.getAllowFlight()) {
    		if (!silent) {
    			if (forced) {
    				String reason = Settings.flightDeactivatedMsg;
    				if (cause == "pvp") 
    					reason = Settings.flightDeactivatedPVPMsg;
    				if (cause == "console") 
    					reason = Settings.flightDeactivatedConsoleMsg;
    				Message.to(player, reason + Settings.flightOffMsg);
    			} else {
    				Message.to(player, Settings.flightOffMsg);
    			}
    		}
    		if (player.isFlying()) {
    			// As of 1.15 the below line does not seem to be reliable.
        		player.setFallDistance(-100000);
        		// As of 1.15 the below is required.
        		if (!player.isOnGround()) {
        			TownyFlightAPI.getInstance().addFallProtection(player);
        			Bukkit.getScheduler().runTaskLater(plugin, ()-> TownyFlightAPI.getInstance().removeFallProtection(player), 100);
        		}
    		}
    		player.setAllowFlight(false);
    	} else {
    		if (!silent) 
    			Message.to(player, Settings.flightOnMsg);
    		player.setAllowFlight(true);
    	}
    }

    public boolean toggleFlightOnOther(CommandSender sender, String name) {

		Player player = Bukkit.getPlayerExact(name);
		if (player != null && player.isOnline())
	    	if (!player.getAllowFlight()) {
	    		Message.to(sender, "Player " + name + " is already unable to fly. Could not remove flight.");
	    	} else {
	    		toggleFlight(player.getPlayer(), false, true, "console");
	    		Message.to(sender, "Flight removed from " + name + ".");
	    	}
		else
			Message.to(sender, "Player " + name + " not found, or is offline. Could not remove flight.");

		return true;
    }
    

    public boolean reloadPlugin(CommandSender sender) {
		plugin.loadSettings();
		plugin.unregisterEvents();
		plugin.registerEvents();
		Message.to(sender, "Config.yml reloaded");
		return true;
    }
}
