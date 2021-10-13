package cz.apigames.betterhud.plugin_old.Events;

import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if(Display.getByPlayer(player) != null) {

            Display.getByPlayer(player).remove();

        }

    }

}
