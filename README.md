# TownyFlight

Finally, you can allow towny residents to fly while in their own towns, for free, from the lead developer of Towny.

__As of version 1.5.0 This plugin supports Towny 0.94.0.2+ and MC 1.14.*.__
__Pre-1.5 versions requires the use of Towny 0.92.0.0 or newer.__

Players that are flying and make a PVP combat attack will have their attack cancelled and their flight taken away. Players that are in creative and make attacks will be allowed to continue flying, but will have their pvp attack canceled.

Players that have their flight taken away will fall harmlessly to the ground.

If you want to support the developer consider [becoming a Sponsor](https://github.com/sponsors/LlmDl).
___

## Commands:

- /tfly - Enable/disable flight.
- /tfly reload - Reload the config.
- /tfly {name} - (Console Only) Removes flight from someone.

---

## Permission nodes:

- townyflight.command.tfly - required to use /tfly.
- townyflight.command.tfly.reload - required to reload the config.
- townyflight.alliedtowns - allows players to use /tfly in towns which consider that player an ally.
- townyflight.alltowns - allows players to use /tfly in any town, but not the wilderness.
- townyflight.bypass - default to Ops, bypasses removal of flight, use /tfly anywhere.

---

## Config:
- Config auto-updates with new features while keeping your old settings intact (like Towny.)
- Includes all language strings in case translation is required.

- options.auto_Enable_Flight
  - default: false
  - If set to true, players entering their town will have flight auto-enabled.
  - When set to true, the plugin will use slightly more resources due to the EnterTown listener.
- options.auto_Enable_Silent
  - default: false
  - If set to true, players entering their town will have flight auto-enabled without being notified in chat
- options.disable_During_Wartime
  - default: true
  - If set to false, players can still fly in their town while war is active.
- options.disable_Combat_Prevention
  - default: false
  - If set to false, TownyFlight will not prevent combat from player who fly or take their flight away.
- options.show_Permission_After_No_Permission_Message
  - default: true
  - If set to false, the language.noPermission message will not display the permission node.

---

I'm open to new features and willing to fix any bugs found.


License: http://creativecommons.org/licenses/by-nc-nd/3.0/
