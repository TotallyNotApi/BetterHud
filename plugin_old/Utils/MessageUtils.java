package cz.apigames.betterhud.plugin_old.Utils;

import cz.apigames.betterhud.BetterHud;
import cz.apigames.betterhud.plugin_old.Configs.ConfigManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static final Pattern pattern = Pattern.compile("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})");

    public static String colorize(String message) {
        if(BetterHud.isHexSupported()) {
            Matcher matcher = pattern.matcher(message);

            while (matcher.find()) {
                String color = message.substring(matcher.start()+1, matcher.end()-1);
                message = message.replace(matcher.group(), ""+ ChatColor.of(color));
            }

        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String translatePlaceholders(String message, Player player) {

        if(BetterHud.isPAPILoaded()) {

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

        for(String placeholder : Placeholders.internalPlaceholders) {
            message = message.replace(placeholder, Placeholders.getInternalPlaceholder(placeholder, player));
        }

        return message;
    }

    public static String getMessage(String messagePath) {
        return colorize(ConfigManager.getConfig("messages.yml").getString("messages."+messagePath));
    }

    public static String getRawMessage(String str) {

        str = str.replaceAll("%(\\S*?)%", "");
        str = str.replaceAll("\\{(\\S*?)}", "");
        str = str.replaceAll("&[0-9a-fk-or]", "");
        str = str.replaceAll("(?<!\\\\)(\\{#[a-fA-F0-9]{6}})", "");

        return str;
    }


}
