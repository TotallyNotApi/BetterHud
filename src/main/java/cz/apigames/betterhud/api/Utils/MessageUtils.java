package cz.apigames.betterhud.api.Utils;

import cz.apigames.betterhud.api.BetterHudAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static final Pattern legacyPattern = Pattern.compile("&[0-9a-fk-or]");
    private static final Pattern hexPattern = Pattern.compile("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})");

    public static String colorize(String message) {
        if(BetterHudAPI.isHexSupported()) {
            Matcher matcher = hexPattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start()+1, matcher.end()-1);
                message = message.replace(matcher.group(), ""+ ChatColor.of(color));
            }

        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String translatePlaceholders(String message, Player player) {

        if(BetterHudAPI.isPapiEnabled()) {

            //FIX TO EXCLUDE TRANSLATING COLORS
            if(PlaceholderAPI.containsPlaceholders(message)) {

                Pattern pattern = Pattern.compile("%(\\S*?)%");
                Matcher matcher = pattern.matcher(message);

                while(matcher.find()) {

                    String placeholder = PlaceholderAPI.setPlaceholders(player, matcher.group());
                    message = message.replace(matcher.group(), placeholder);

                }

            }

        }

        for(String placeholder : BetterHudPlaceholders.placeholders) {
            message = message.replace(placeholder, BetterHudPlaceholders.getPlaceholder(placeholder, player));
        }

        return message;
    }

    public static boolean isLegacyColorCode(String message, int index) {
        String colorCode = message.substring(index, index+2);
        Matcher matcher = legacyPattern.matcher(colorCode);
        return matcher.find();
    }

    public static boolean isHexColorCode(String message, int index) {
        String colorCode = message.substring(index, index+9);
        Matcher matcher = hexPattern.matcher(colorCode);
        return matcher.find();
    }

    public static String getCharNameFromPath(String path) {

        String[] split = path.split("/");

        return split[split.length-1].split("\\.")[0];

    }

    public static String stripColors(String str) {

        str = str.replaceAll("&[0-9a-fk-or]", "");
        str = str.replaceAll("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})", "");

        return str;

    }

    public static String getRawMessage(String str) {

        str = str.replaceAll("%(\\S*?)%", "");
        str = str.replaceAll("\\{(\\S*?)}", "");
        str = str.replaceAll("&[0-9a-fk-or]", "");
        str = str.replaceAll("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})", "");

        return str;
    }

}
