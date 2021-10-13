package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays;

import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import org.bukkit.entity.Player;

public class Placeholder {

    public static String get(Player player, Hud hud) {
        return Display.getHudContent(player, hud);
    }

}
