package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.Displays.Display;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

class PlayerLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        Display.getDisplays(event.getPlayer()).forEach(Display::destroy);

    }

}
