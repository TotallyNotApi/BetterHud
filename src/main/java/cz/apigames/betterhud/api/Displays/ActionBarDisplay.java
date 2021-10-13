package cz.apigames.betterhud.api.Displays;

import cz.apigames.betterhud.api.BetterHudAPI;
import cz.apigames.betterhud.api.Hud;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarDisplay extends Display {


    public ActionBarDisplay(Player player, Hud hud) {
        super(player, hud);
    }

    @Override
    public void init() {

        super.taskID = Bukkit.getScheduler().runTaskTimerAsynchronously(BetterHudAPI.getPlugin(), this::render, 0, (hud.getRefreshInterval()/1000)*20).getTaskId();

    }

    @Override
    public void render() {

        if(player != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(hud.getRenderedString(player)));
        }

    }
}
