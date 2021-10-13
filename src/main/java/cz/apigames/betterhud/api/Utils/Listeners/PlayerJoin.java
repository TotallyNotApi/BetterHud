package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        BetterHudAPI.getLoadedHuds().forEach(hud -> {

            Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.PLAYER_JOIN)).findFirst();
            optEvent.ifPresent(toggleEvent -> hud.renderFor(event.getPlayer(), toggleEvent.getDisplayType(), toggleEvent.getHideAfter()));

        });

    }

}
