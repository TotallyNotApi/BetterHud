package cz.apigames.betterhud.plugin_old.Events.CustomEvents;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerEntersWaterEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerEntersWaterEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
