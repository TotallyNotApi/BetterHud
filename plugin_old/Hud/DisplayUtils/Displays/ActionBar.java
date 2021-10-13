package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;

public class ActionBar {

    private static HashMap<Player, ActionBar> playerMap = new HashMap<>();
    private Player player;
    private Hud displayHud;

    public ActionBar(Player player, Hud displayHud) {

        if(!playerMap.containsKey(player)) {
            this.player = player;
            this.displayHud = displayHud;
            playerMap.put(player, this);
        } else {
            getByPlayer(player).displayHud = displayHud;
        }

    }

    public static ActionBar getByPlayer(Player player) {
        return playerMap.get(player);
    }

    public void hide() {

        playerMap.remove(player);

    }

    public static Collection<ActionBar> getAll() {
        return playerMap.values();
    }

    public void update() {

        BaseComponent baseComponent = new TextComponent(Display.getHudContent(player, displayHud));

        //RUN SYNC
        Bukkit.getScheduler().runTask(BetterHud.getPlugin(), () -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, baseComponent));
    }



}
