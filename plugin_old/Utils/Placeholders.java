package cz.apigames.betterhud.plugin_old.Utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Placeholders {

    public static List<String> internalPlaceholders = Arrays.asList("{health}", "{food}", "{armor}", "{oxygen}", "{health-formatted}");

    public static String getInternalPlaceholder(String placeholder, Player player) {

        if(placeholder.equalsIgnoreCase("{health}")) {

            return String.valueOf(Math.floor(player.getHealth()));

        }
        if(placeholder.equalsIgnoreCase("{health-formatted}")) {

            return String.valueOf(Math.floor(player.getHealth())).split("\\.")[0];

        }
        if(placeholder.equalsIgnoreCase("{food}")) {

            return String.valueOf(Math.floor(player.getFoodLevel())).split("\\.")[0];

        }
        if(placeholder.equalsIgnoreCase("{armor}")) {

            return String.valueOf(Math.floor(player.getAttribute(Attribute.GENERIC_ARMOR).getValue())).split("\\.")[0];

        }
        if(placeholder.equalsIgnoreCase("{oxygen}")) {

            return String.valueOf(Math.floor(getBubbles(player.getRemainingAir()))).split("\\.")[0];

        }
        return "";

    }

    private static int getBubbles(int airLevel) {
        if(airLevel < 0)
            return 0;
        return ((airLevel-3)/30)+1;
    }

}
