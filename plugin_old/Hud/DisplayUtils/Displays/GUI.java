package cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Displays;

import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import cz.apigames.betterhud.plugin_old.Hud.DisplayUtils.Display;
import cz.apigames.betterhud.plugin_old.Hud.Hud;
import org.bukkit.entity.Player;

public class GUI {

    public static String get(Player player, Hud hud) {

        String output = Display.getHudContent(player, hud);

        String offset = hud.getGuiTitleOffset() == 0 ? "" : ":offset_"+hud.getGuiTitleOffset()+":";

        if(ConfigManager.getConfig("config.yml").isSet("configuration.huds."+hud.getName()+".position")) {

            if(ConfigManager.getConfig("config.yml").getString("configuration.huds."+hud.getName()+".position").equalsIgnoreCase("after")) {
                output = offset+hud.getGuiTitle()+output;
            } else {
                output = output+offset+hud.getGuiTitle();
            }

        } else {
            output = output+offset+hud.getGuiTitle();
        }

        return output;
    }

}
