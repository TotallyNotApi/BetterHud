package cz.apigames.betterhud.plugin_old.Events;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.PluginSetup.Setup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if(!BetterHud.isPluginSetup()) {
            if(player.isOp()) {
                Bukkit.getScheduler().runTaskLater(BetterHud.getPlugin(), () -> Setup.welcomeMessage(player), 60L);
            }
        }

    }

}
