package cz.apigames.betterhud.api.Utils.Listeners;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Utils.ToggleEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.Optional;

public class GamemodeChange implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerGameModeChangeEvent event) {

        BetterHudAPI.getLoadedHuds().forEach(hud -> {

            Optional<ToggleEvent> optEvent = hud.getEvents().stream().filter(toggleEvent -> toggleEvent.getEventType().equals(ToggleEvent.EventType.GAMEMODE_CHANGE)).findFirst();

            if(optEvent.isPresent()) {

                if(optEvent.get().getOpt_value() != null) {

                    //TO
                    if(event.getNewGameMode().name().equalsIgnoreCase(optEvent.get().getOpt_value())) {
                        hud.renderFor(event.getPlayer(), optEvent.get().getDisplayType(), optEvent.get().getHideAfter());
                    }

                    //FROM
                    else if(event.getPlayer().getGameMode().name().equalsIgnoreCase(optEvent.get().getOpt_value())) {
                        hud.hide(event.getPlayer());
                    }
                }

            }

        });


    }


}
