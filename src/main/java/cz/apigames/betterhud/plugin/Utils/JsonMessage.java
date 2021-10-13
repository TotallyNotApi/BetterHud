package cz.apigames.betterhud.plugin.Utils;

import cz.apigames.betterhud.api.Utils.MessageUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

public class JsonMessage {

    public static void sendMessage(Player player, String suggest_command, String message, String hover) {

        TextComponent text = new TextComponent(MessageUtils.colorize(message));
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageUtils.colorize(hover))));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest_command));
        player.spigot().sendMessage(text);

    }
}
